package seedu.saveit.ui;

import java.time.LocalDate;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import seedu.saveit.commons.core.GuiSettings;
import seedu.saveit.commons.core.LogsCenter;
import seedu.saveit.logic.Logic;
import seedu.saveit.logic.commands.CommandResult;
import seedu.saveit.logic.commands.exceptions.CommandException;
import seedu.saveit.logic.parser.exceptions.ParseException;
import seedu.saveit.ui.exceptions.PrinterException;


/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ExpenditureListPanel expenditureListPanel;
    private ResultDisplay resultDisplay;
    //private HelpWindow helpWindow;
    private ReportWindow reportWindow;
    private ActiveNameAndDateView activeNameAndDateView;
    private CalendarView calendarView;
    private BudgetView budgetView;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane expenditureListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane calendar;

    @FXML
    private StackPane activeAccountNamePlaceHolder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane budgetPlaceHolder;


    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        //helpWindow = new HelpWindow();
        reportWindow = new ReportWindow();
        reportWindow.addLogic(logic);

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     *
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        expenditureListPanel = new ExpenditureListPanel(logic.getFilteredBaseExpList());
        expenditureListPanelPlaceholder.getChildren().add(expenditureListPanel.getRoot());
        calendarView = new CalendarView(this::executeCommand);
        calendar.getChildren().add(calendarView.getRoot());
        activeNameAndDateView = new ActiveNameAndDateView(logic.getAddressBook().getActiveAccountName(),
                logic.getAddressBook().getActiveDate());
        activeAccountNamePlaceHolder.getChildren().add(activeNameAndDateView.getRoot());
        budgetView = new BudgetView();
        budgetPlaceHolder.getChildren().add(budgetView.getRoot());
        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        try {
            executeCommand("help");
        } catch (CommandException | ParseException | PrinterException e) {
            return;
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);

        reportWindow.hide();
        //helpWindow.hide();
        primaryStage.hide();
    }

    public ExpenditureListPanel getExpenditureListPanel() {
        return expenditureListPanel;
    }

    /**
     * Opens a report window.
     */
    @FXML
    private void handleReport() {

        assert reportWindow.isShowing() == false;

        reportWindow.showEmpty();

    }

    /**
     * This method is called to ensure all the view are displaying the right stuff
     * when the app is just opened up.
     */
    public void refreshAtStart() {
        try {
            this.executeCommand("go " + LocalDate.now().toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.saveit.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException, PrinterException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isExit()) {
                handleExit();
            }

            if (commandResult.isShowReport()) {
                reportWindow.showResult(commandResult);
            }

            if (commandResult.isUpdateCalendar()) {
                calendarView.updateActiveDate(commandResult.getNewActiveDate());
                activeNameAndDateView.setActiveDate(commandResult.getNewActiveDate());
            }

            if (commandResult.isUpdateAccountName()) {
                activeNameAndDateView.setActiveAccountName(commandResult.getActiveAccountName());
            }

            if (commandResult.isUpdateBudgetView()) {
                Boolean isExist = commandResult.getBudget() != null;
                budgetView.setBudgetExist(isExist);
                if (isExist) {
                    budgetView.setBudgetAmount(commandResult.getBudget());
                    budgetView.setTotalSpending(commandResult.getTotalSpending());
                    budgetView.updateView();
                } else {
                    budgetView.updateView();
                }
            }

            return commandResult;
        } catch (CommandException | ParseException e) {

            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

}
