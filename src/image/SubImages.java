package image;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Sub images iterator to be used for iterating over all the sub images
 */
public class SubImages implements Iterable<Color[][]>{
    private final ArrayList<ArrayList<Color[][]>> subImagesCollection;

    /**
     * Constructs a new SubImages instance
     * @param subImages sub images collection to be iterated on
     */
    public SubImages(ArrayList<ArrayList<Color[][]>> subImages){
        this.subImagesCollection = subImages;
    }

    /**
     * Iterator of the sub images collection
     * @return Iterator over the sub images collection
     */
    @Override
    public Iterator<Color[][]> iterator() {
        return new Iterator<>() {
            private int row = 0;
            private int col = 0;

            @Override
            public boolean hasNext() {
                return row != subImagesCollection.size() && col != subImagesCollection.get(0).size();
            }

            @Override
            public Color[][] next() {
                if (!hasNext()){
                    throw new NoSuchElementException();
                }
                Color[][] element = subImagesCollection.get(row).get(col);
                col++;
                if(col >= subImagesCollection.get(0).size()){
                    row++;
                    col = 0;
                }
                return element;
            }
        };
    }

}
