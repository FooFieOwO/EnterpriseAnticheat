package dev.brighten.ac.utils.menu.preset.button;

import dev.brighten.ac.utils.ItemBuilder;
import dev.brighten.ac.utils.XMaterial;
import dev.brighten.ac.utils.menu.button.Button;

public class FillerButton extends Button {

    public FillerButton() {
        super(false, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).name(" ")
                .durability(15).build());
    }
}
