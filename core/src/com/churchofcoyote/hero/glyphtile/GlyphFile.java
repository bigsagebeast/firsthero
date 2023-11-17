package com.churchofcoyote.hero.glyphtile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GlyphFile {
    public int rows, columns;
    public String[][] glyphName;
    public Texture texture;

    public GlyphFile(String filename) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(filename).read()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            String dimensionLine = reader.readLine();
            String[] dimensions = dimensionLine.split(" ");
            rows = Integer.valueOf(dimensions[0]);
            columns = Integer.valueOf(dimensions[1]);
            glyphName = new String[rows][];
            for (int i=0; i<rows; i++) {
                glyphName[i] = new String[columns];
            }

            String entryLine = reader.readLine();
            while (entryLine != null) {
                String[] entry = entryLine.split(" ");
                int row = Integer.valueOf(entry[0]);
                int column = Integer.valueOf(entry[1]);
                String name = entry[2];
                glyphName[row][column] = name;

                entryLine = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        String imageName = filename.split("\\.")[0] + ".png";

        texture = new Texture(Gdx.files.internal(imageName));
    }


}
