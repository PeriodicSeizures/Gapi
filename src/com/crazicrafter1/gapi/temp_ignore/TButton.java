package com.crazicrafter1.gapi.temp_ignore;

import com.crazicrafter1.gapi.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

/**
 * The functionality of this class is very complicated
 * should scrap, to something that makes sense
 * @deprecated
 */
public class TButton extends Button {

    //public static class TInteract extends Interact {
    //    public String text;

    //    public TInteract(Player player,
    //                     ItemStack heldItem,
    //                     ItemStack clickedItem,
    //                     boolean shift) {
    //        super(player, heldItem, clickedItem, shift);

    //        this.text = clickedItem.hasItemMeta() ?
    //                clickedItem.getItemMeta().getDisplayName() :
    //                "";
    //    }
    //}

    TButton(ItemStack itemStack,
                   Function<Interact, Result> leftClickListener) {
        super(itemStack, leftClickListener, null);
    }

    public String getText() {
        return itemStack.hasItemMeta() ?
                itemStack.getItemMeta().getDisplayName() :
                "";
    }

    public static class TBuilder extends Button.Builder {



    }

}
