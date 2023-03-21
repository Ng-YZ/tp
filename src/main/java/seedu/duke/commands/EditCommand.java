package seedu.duke.commands;

import seedu.duke.exceptions.MissingParametersException;
import seedu.duke.objects.Inventory;
import seedu.duke.objects.Item;
import seedu.duke.utils.Ui;
import seedu.duke.exceptions.EditErrorException;

import java.util.ArrayList;

/**
 * Represents the command to edit an item in the inventory.
 */
public class EditCommand extends Command {

    private String[] editInfo;

    public EditCommand(Inventory inventory, String[] editInfo) {
        super(inventory);
        this.editInfo = editInfo;
    }

    /**
     * Searches the Hashmap to obtain the item required to be interacted with by the user.
     *
     * @param editInfo The array of strings that contain the user inputs.
     * @return Returns the variable of type "Item", which is the item in question to be interacted with by the user.
     * @throws EditErrorException Exception related to all errors generated by the edit command.
     */
    private Item retrieveItemFromHashMap(final String[] editInfo) throws EditErrorException {
        String upcCode = editInfo[0].replaceFirst("upc/", "");
        if (!upcCodes.containsKey(upcCode)) {
            throw new EditErrorException();
        }
        Item selectedItem = upcCodes.get(upcCode);
        return selectedItem;
    }

    /**
     * Executes method to edit item attributes in the list and prints an error string if the user's edit command
     * inputs were incorrectly written.
     *
     * @param item The target item in the ArrayList in which the user wants to edit.
     * @param data The user input which contains the information to be used to update the item attributes.
     * @throws MissingParametersException Exception related to all errors due to missing parameters.
     * @throws NumberFormatException Exception related to all invalid number formats inputted.
     */
    private void updateItemInfo(final Item item, final String data) throws MissingParametersException,
            NumberFormatException {
        try {
            handleUserEditCommands(item, data);
        } catch (MissingParametersException mpe) {
            throw new MissingParametersException();
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException();
        }
    }

    /**
     * Detects specific chars in the array of individual strings, and executes the change of item attribute values
     * (i.e, Name, Quantity, Price) based on the first few chars detected in the individual string.
     *
     * @param item The target item in the ArrayList in which the user wants to edit.
     * @param data The user input which contains the information to be used to update the item attributes.
     * @throws MissingParametersException Exception related to all errors due to missing parameters.
     * @throws NumberFormatException Exception related to all invalid number formats inputted.
     */
    private void handleUserEditCommands(Item item, String data) throws MissingParametersException,
            NumberFormatException {
        if (data.contains("n/")) {
            String newName = data.replaceFirst("n/", "");
            item.setName(newName);
        } else if (data.contains("qty/")) {
            String updatedQuantity = data.replaceFirst("qty/", "");
            try {
                Integer newQuantity = Integer.valueOf(updatedQuantity);
                item.setQuantity(newQuantity);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException();
            }
        } else if (data.contains("p/")) {
            String updatedPrice = data.replaceFirst("p/", "");
            try {
                Double newPrice = Double.valueOf(updatedPrice);
                item.setPrice(newPrice);
            } catch (NumberFormatException nfe) {
                throw new NumberFormatException();
            }
        } else {
            throw new MissingParametersException();
        }
    }

    /**
     * Edit Command that searches for the item in the ArrayList and changes the item attributes according
     * to the wishes of the user.
     */
    public void setEditInfo() {
        try {
            Item updatedItem = retrieveItemFromHashMap(editInfo);
            Item oldItem = new Item(updatedItem.getName(), updatedItem.getUpc(), updatedItem.getQuantity().toString(),
                    updatedItem.getPrice().toString());
            for (int data = 1; data < editInfo.length; data += 1) {
                updateItemInfo(updatedItem, editInfo[data]);
            }
            handleTrie(updatedItem, oldItem);
            upcCodes.remove(oldItem.getUpc());
            upcCodes.put(updatedItem.getUpc(), updatedItem);
            Ui.printEditDetails(oldItem, updatedItem);
        } catch (EditErrorException eee) {
            Ui.printItemNotFound();
        } catch (MissingParametersException mpe) {
            Ui.printInvalidEditCommand();
        } catch (NumberFormatException nfe) {
            Ui.printInvalidPriceOrQuantityEditInput();
        }
    }

    private void handleTrie(Item updatedItem, Item oldItem) {
        String oldItemName = oldItem.getName().toLowerCase();
        String newItemName = updatedItem.getName().toLowerCase();
        if (!oldItemName.equals(newItemName) && itemNameHash.get(oldItemName).size() == 1) {
            itemNameHash.remove(oldItemName);
            itemsTrie.remove(oldItemName);
            ArrayList<Item> newItemArrayList = new ArrayList<>();
            newItemArrayList.add(updatedItem);
            itemNameHash.put(newItemName, newItemArrayList);
        } else {
            itemNameHash.get(oldItemName).remove(oldItem);
            if (!itemNameHash.containsKey(newItemName)) {
                itemNameHash.put(newItemName, new ArrayList<Item>());
            }
            itemNameHash.get(newItemName).add(updatedItem);
        }
        itemsTrie.add(newItemName);
    }

    /**
     * Executes the Edit Command
     */
    @Override
    public void run() {
        setEditInfo();
    }
}