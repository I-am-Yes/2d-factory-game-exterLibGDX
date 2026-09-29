package core.machine;

import core.machine.utils.Item;
import core.utils.Artist;


public class ItemRender {

    public void drawBatch(Item item) {
        if (item == null) {
            return;
        }
        Artist.DrawItem(item);
    }

}
