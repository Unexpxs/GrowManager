package src;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorAutoCloseTrigger;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration;
import com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PlantManagementSystem {
    public static void main(String[] args) {
        new PlantManagementSystem().start();
    }

    private final List<Room> rooms = new ArrayList<>();

    public void start() {
        try {
            // Create a Swing terminal frame with increased size
            SwingTerminalFrame terminalFrame = new SwingTerminalFrame(
                    "Plant Management System",
                    new TerminalSize(100, 40),
                    TerminalEmulatorDeviceConfiguration.getDefault(),
                    SwingTerminalFontConfiguration.getDefault(),
                    TerminalEmulatorColorConfiguration.getDefault(),
                    TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode
            );

            terminalFrame.setVisible(true);

            Screen screen = new com.googlecode.lanterna.screen.TerminalScreen(terminalFrame);
            screen.startScreen();
            MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen) {
                @Override
                public boolean handleInput(KeyStroke key) {
                    if (key.getKeyType() == KeyType.Character) {
                        switch (key.getCharacter()) {
                            case 'e':
                                if (rooms.size() > 0) {
                                    editRoom(rooms.get(0), this, (BasicWindow) getActiveWindow());
                                    return true;
                                }
                                break;
                            case 'c':
                                createRoom(this, (BasicWindow) getActiveWindow());
                                return true;
                            case 'x':
                                getActiveWindow().close();
                                return true;
                        }
                    }
                    return super.handleInput(key);
                }
            };

            // Main menu window
            BasicWindow mainWindow = new BasicWindow("Plant Management System");
            Panel mainPanel = new Panel(new GridLayout(2));
            updateMainMenu(mainPanel, textGUI, mainWindow);
            mainWindow.setComponent(mainPanel);

            textGUI.addWindowAndWait(mainWindow);

            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMainMenu(Panel mainPanel, WindowBasedTextGUI textGUI, BasicWindow mainWindow) {
        mainPanel.removeAllComponents();
        mainPanel.addComponent(new Label("Rooms:"));
        for (Room room : rooms) {
            mainPanel.addComponent(new Label(room.getName() + " (" + room.getPlants().size() + " plants)"));
            mainPanel.addComponent(new Button("Edit", () -> editRoom(room, textGUI, mainWindow)));
        }
        mainPanel.addComponent(new EmptySpace(new TerminalSize(0, 1))); // Empty space for layout
        mainPanel.addComponent(new Button("Create Room", () -> createRoom(textGUI, mainWindow)));
        mainPanel.addComponent(new Button("Exit", mainWindow::close));
    }

    private void createRoom(WindowBasedTextGUI textGUI, BasicWindow mainWindow) {
        String roomName = new TextInputDialogBuilder()
                .setTitle("Create Room")
                .setDescription("Enter room name:")
                .setInitialContent("")
                .build()
                .showDialog(textGUI);
        if (roomName != null && !roomName.trim().isEmpty()) {
            rooms.add(new Room(roomName));
            updateMainMenu((Panel) mainWindow.getComponent(), textGUI, mainWindow);
        }
    }

    private void editRoom(Room room, WindowBasedTextGUI textGUI, BasicWindow mainWindow) {
        BasicWindow editWindow = new BasicWindow("Edit Room: " + room.getName());
        Panel editPanel = new Panel(new GridLayout(3)); // Increased columns to align buttons
        updateEditRoomMenu(editPanel, room, textGUI, editWindow);
        editWindow.setComponent(editPanel);
        textGUI.addWindowAndWait(editWindow);
        updateMainMenu((Panel) mainWindow.getComponent(), textGUI, mainWindow);
    }

    private void updateEditRoomMenu(Panel editPanel, Room room, WindowBasedTextGUI textGUI, BasicWindow editWindow) {
        editPanel.removeAllComponents();
        editPanel.addComponent(new Label("Plants:"));
        for (Plant plant : room.getPlants()) {
            editPanel.addComponent(new Label(plant.getName()));
            editPanel.addComponent(new EmptySpace(new TerminalSize(0, 1))); // Empty space for alignment
            editPanel.addComponent(new Button("Remove", () -> {
                room.getPlants().remove(plant);
                updateEditRoomMenu(editPanel, room, textGUI, editWindow);
            }));
        }
        editPanel.addComponent(new EmptySpace(new TerminalSize(0, 1))); // Empty space for layout
        editPanel.addComponent(new Button("Add Plant", () -> addPlant(room, textGUI, editWindow)));
        editPanel.addComponent(new Button("Back", editWindow::close));
    }

    private void addPlant(Room room, WindowBasedTextGUI textGUI, BasicWindow editWindow) {
        String plantName = new TextInputDialogBuilder()
                .setTitle("Add Plant")
                .setDescription("Enter plant name:")
                .setInitialContent("")
                .build()
                .showDialog(textGUI);
        if (plantName != null && !plantName.trim().isEmpty()) {
            room.getPlants().add(new Plant(plantName));
            updateEditRoomMenu((Panel) editWindow.getComponent(), room, textGUI, editWindow);
        }
    }
}
