package io.github.syst3ms.skriptparser.parsing;

import io.github.syst3ms.skriptparser.file.FileElement;
import io.github.syst3ms.skriptparser.file.FileParser;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.file.VoidElement;
import io.github.syst3ms.skriptparser.lang.event.SkriptEventManager;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.UnloadedTrigger;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import io.github.syst3ms.skriptparser.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Contains the logic for loading, parsing and interpreting entire script files
 */
public class ScriptLoader {
    // using ConcurrentHashMap here to support async script loading in the future
    private static final ConcurrentMap<Path, Script> LOADED_SCRIPTS = new ConcurrentHashMap<>();

    public static ScriptLoadResult getOrLoadScript(Path scriptPath, boolean debug) {
        if(LOADED_SCRIPTS.containsKey(scriptPath)) {
            return new ScriptLoadResult(LOADED_SCRIPTS.get(scriptPath));
        }

        return loadScript(scriptPath, debug);
    }

    public static Optional<Script> getScript(Path path) {
        return Optional.ofNullable(LOADED_SCRIPTS.get(path));
    }


    /**
     * Loads a Script. If the given script is already loaded, it will simply return without loading again
     *
     * @param path  The path to the script
     * @param debug flag if debug mode should be used.
     * @return The ScriptLoadResult, containing the script and the logs.
     */
    public static ScriptLoadResult loadScript(@NotNull Path path, boolean debug) {
        var logger = new SkriptLogger(debug);

        Script script;
        // make sure we don't have a script at the path already loaded
        if(getScript(path).isPresent()) {
            // if we do, check if its loaded
            script = getScript(path).get();
            if(script.isLoaded()) {
                // loaded scripts will directly return - we don't need to load them no more
                return new ScriptLoadResult(script);
            }
        } else {
            // create a new Script instance if there is no Script already present.
            String scriptName = path.getFileName().toString().replaceAll("(.+)\\..+", "$1");
            script = new Script(null, path, scriptName);
            LOADED_SCRIPTS.put(path, script);
        }


        // load the elements into the script
        return loadScript(script, logger);
    }


    /**
     * Loads FileElements into a script. This is only possible if the script is unloaded.
     * Instead of using this method, you should use {@link Script#reload()} or {@link #getOrLoadScript(Path, boolean)}
     *
     * @param script The script to load the elements into
     * @param logger the logger to direct logs to
     * @return a ScriptLoadResult containing the Script as well as all lgs
     * @see Script#reload()
     * @see #getOrLoadScript(Path, boolean)
     */
    public static ScriptLoadResult loadScript(@NotNull Script script, SkriptLogger logger) {

        // read the file and parse the elements
        List<String> lines;

        // read the liens within the file
        try {
            lines = FileUtils.readAllLines(script.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new ScriptLoadResult(null);
        }

        // parse the lines into FileElements
        List<FileElement> elements = FileParser.parseFileLines(script.getName(),
                lines,
                0,
                1,
                logger
        );

        return loadScript(script, elements, logger);
    }

    /**
     * Loads FileElements into a script. This is only possible if the script is unloaded.
     * Instead of using this method, you should use {@link Script#reload()} or {@link #getOrLoadScript(Path, boolean)}
     *
     * @param script   The script to load the elements into
     * @param elements The elements to load into the script.
     * @return a ScriptLoadResult containing the Script as well as all lgs
     * @see Script#reload()
     * @see #getOrLoadScript(Path, boolean)
     */
    public static ScriptLoadResult loadScript(@NotNull Script script, @NotNull List<FileElement> elements, SkriptLogger logger) {
        if(script.isLoaded()) {
            throw new IllegalStateException("Tried loading elements into a loaded script file!");
        }

        logger.setFileInfo(script, elements);
        List<UnloadedTrigger> unloadedTriggers = new ArrayList<>();


        // collect structures/events and parse them
        for (var element : elements) {
            logger.nextLine();
            if(element instanceof VoidElement)
                continue;
            if(!(element instanceof FileSection)) {
                logger.error("Can't have code outside of a trigger", ErrorType.STRUCTURE_ERROR);
            }


            // parse the contents of the trigger
            assert element instanceof FileSection;
            var trig = SyntaxParser.parseTrigger((FileSection) element, logger);
            trig.ifPresent(t -> {
                logger.setLine(logger.getLine() + ((FileSection) element).length());
                unloadedTriggers.add(t);
            });
        }

        // sort to assure triggers are loaded in order
        unloadedTriggers.sort((a, b) -> b.getTrigger().getEvent().getLoadingPriority() - a.getTrigger().getEvent().getLoadingPriority());

        // loops all the structures/events inside the file
        for (UnloadedTrigger unloaded : unloadedTriggers) {
            // resets logger
            logger.setLine(unloaded.getLine());

            // gets the trigger, the object holding the code of the structure
            Trigger loaded = unloaded.getTrigger();
            unloaded.getParserState().setCurrentEvent(loaded.getEvent());
            loaded.loadSection(unloaded.getSection(), unloaded.getParserState(), logger);

            // Why does the addon handle trigger handling???
            // what's the point of init method??
            unloaded.getEventInfo().getRegisterer().handleTrigger(loaded);

            loaded.getEvent().register(loaded, SkriptEventManager.GLOBAL_EVENT_MANAGER);
        }

        // finally, load the script with its new set of triggers.
        Set<Trigger> triggers = unloadedTriggers
                .stream()
                .map(UnloadedTrigger::getTrigger)
                .collect(Collectors.toUnmodifiableSet());

        script.load(triggers);

        return new ScriptLoadResult(logger.close(), script);
    }

    /**
     * Parses all items inside of a given section.
     * @param section the section
     * @param logger the logger
     * @return a list of {@linkplain Statement effects} inside of the section
     */
    public static List<Statement> loadItems(FileSection section, ParserState parserState, SkriptLogger logger) {
        logger.recurse();
        parserState.recurseCurrentStatements();
        List<Statement> items = new ArrayList<>();
        var elements = section.getElements();
        for (var element : elements) {
            logger.finalizeLogs();
            logger.nextLine();
            if (element instanceof VoidElement)
                continue;
            if (element instanceof FileSection) {
                var codeSection = SyntaxParser.parseSection((FileSection) element, parserState, logger);
                if (codeSection.isEmpty()) {
                    continue;
                }

                parserState.addCurrentStatement(codeSection.get());
                items.add(codeSection.get());
            } else {
                var statement = SyntaxParser.parseEffect(element.getLineContent(), parserState, logger);
                if (statement.isEmpty())
                    continue;

                parserState.addCurrentStatement(statement.get());
                items.add(statement.get());
            }
        }
        logger.finalizeLogs();
        for (var i = items.size() - 1; i > 0; i--) {
            items.get(i - 1).setNext(items.get(i));
        }
        logger.callback();
        parserState.callbackCurrentStatements();
        return items;
    }

}