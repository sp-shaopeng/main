package seedu.address.logic.parser.account;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.general.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AccLevelParser {
    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command accParseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {
        /*
        case ExpAddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case ExpEditCommand.COMMAND_WORD:
            return new ExpEditCommandParser().parse(arguments);

        case ExpDeleteCommand.COMMAND_WORD:
            return new ExpDeleteCommandParser().parse(arguments);

        case AccClearCommand.COMMAND_WORD:
            return new AccClearCommand();

        case ExpFindCommand.COMMAND_WORD:
            return new ExpFindCommandParser().parse(arguments);

        case AccListCommand.COMMAND_WORD:
            return new AccListCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        */
        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}
