package com.writercorporation.maudit;

/**
 * Created by priyanka.nandkar on 4/1/2017.
 */

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Color;


public class TextCaptcha extends Captcha {

    protected TextOptions options;
    private int wordLength;
    private char mCh;

    public enum TextOptions {
        UPPERCASE_ONLY,
        LOWERCASE_ONLY,
        NUMBERS_ONLY,
        LETTERS_ONLY,
        NUMBERS_AND_LETTERS
    }

    public TextCaptcha(int wordLength, TextOptions opt) {
        new TextCaptcha(0, 0, wordLength, opt);
    }

    public TextCaptcha(int width, int height, int wordLength, TextOptions opt) {
        setHeight(height);
        setWidth(width);
        this.options = opt;
        usedColors = new ArrayList<>();
        this.wordLength = wordLength;
        this.image = image();
    }

    @Override
    protected Bitmap image() {
        LinearGradient gradient = new LinearGradient(0, 0, 400, getHeight() / 2, color(), color(), Shader.TileMode.MIRROR);// changed from getWidth() to 400
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawRect(0, 0, 400, getHeight(), p);// changed from getWidth() to 400
        Paint tp = new Paint();
        tp.setDither(true);
        tp.setTextSize(getWidth() / getHeight() * 20);

        Random r = new Random(System.currentTimeMillis());
        CharArrayWriter cab = new CharArrayWriter();
        this.answer = "";
        for (int i = 0; i < this.wordLength; i++) {
            char ch = ' ';
            switch (options) {
                case UPPERCASE_ONLY:
                    ch = (char) (r.nextInt(91 - 65) + (65));
                    break;
                case LOWERCASE_ONLY:
                    ch = (char) (r.nextInt(123 - 97) + (97));
                    break;
                case NUMBERS_ONLY:
                    ch = (char) (r.nextInt(58 - 49) + (49));
                    break;
                case LETTERS_ONLY:
                    ch = getLetters(r);
                    break;
                case NUMBERS_AND_LETTERS:
                default:
                    ch = getLettersNumbers(r);
                    break;
            }
            cab.append(ch);
            this.answer += ch;
        }

        char[] data = cab.toCharArray();
        for (int i = 0; i < data.length; i++) {
            this.x += (50 - (3 * this.wordLength)) + (Math.abs(r.nextInt()) % (65 - (1.2 * this.wordLength)));// changed 30 to 50
            this.y = 70 + Math.abs(r.nextInt()) % 70;//changed this from 50 to 70
            Canvas cc = new Canvas(bitmap);
            tp.setTextSkewX(r.nextFloat() - r.nextFloat());
            tp.setColor(Color.BLACK);
            cc.drawText(data, i, 1, this.x, this.y, tp);
            tp.setTextSkewX(0);
        }
        return bitmap;
    }

    private char getLetters(Random r) {
        int rint = (r.nextInt(123 - 65) + (65));
        if (((rint > 90) && (rint < 97)))
            getLetters(r);
        else
            mCh = (char) rint;
        return mCh;
    }

    private char getLettersNumbers(Random r) {
        int rint = (r.nextInt( 123- 49) + (49));

        if (((rint > 90) && (rint < 97)))
            getLettersNumbers(r);
        else if (((rint > 57) && (rint < 65)))
            getLettersNumbers(r);
        else
            mCh = (char) rint;
        return mCh;
    }
}