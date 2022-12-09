package image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SubImages implements Iterable<Color[][]>{
    private final ArrayList<ArrayList<Color[][]>> subImages;

    public SubImages(ArrayList<ArrayList<Color[][]>> subImages){
        this.subImages = subImages;
    }

    @Override
    public Iterator<Color[][]> iterator() {
        return new Iterator<>() {
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext() {
                return row != subImages.size() && col != subImages.get(0).size();
            }

            @Override
            public Color[][] next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                Color[][] element = subImages.get(row).get(col);
                col++;
                if(col >= subImages.get(0).size()){
                    row++;
                    col = 0;
                }
                return element;
            }
        };
    }

}
