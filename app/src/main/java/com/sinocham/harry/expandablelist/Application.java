package com.sinocham.harry.expandablelist;

import java.util.ArrayList;

/**
 * Created by Harry on 1/25/2016.
 */
public class Application extends android.app.Application {
    public static ArrayList<ArrayList<String[]>> speech = new ArrayList<>();
    public static ArrayList<String[]> jokesAll = new ArrayList<>();
    public static ArrayList<String[]> educationsAll = new ArrayList<>();
    public static ArrayList<String[]> commercialAll = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        speech.add(jokesAll);
        speech.add(educationsAll);
        speech.add(commercialAll);
        jokesAll.add(Constant.jokes);
        jokesAll.add(Constant.jokesCn);
        jokesAll.add(Constant.jokesEng);

        educationsAll.add(Constant.educations);
        educationsAll.add(Constant.educationsCn);
        educationsAll.add(Constant.educationsEng);

        commercialAll.add(Constant.commercial);
        commercialAll.add(Constant.commercialCn);
        commercialAll.add(Constant.commercialEng);
    }
}
