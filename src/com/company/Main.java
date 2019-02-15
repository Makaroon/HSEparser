package com.company;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {

    public static Elements brokenPages = new Elements();

    public static Elements visitedPages = new Elements();

    public static int uniquePagesNum = 0;


    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("http://hse.ru/").get();
        findBroken(doc);
        System.out.println("Всего ссылок: " + uniquePagesNum);
        System.out.println("Битых ссылок: " + brokenPages.size());
    }

    private static void findBroken(Element e) throws org.jsoup.HttpStatusException {
        boolean error = false;
        boolean connected = false;
        Document doc = new Document(e.baseUri());
        try {
            System.out.println(e.attr("href"));
            doc = Jsoup.connect(e.attr("href")).get();
            connected = true;
        } catch (HttpStatusException ex1) {
            brokenPages.add(e);
            error = true;
            System.out.println("!!!!!!!!!!!!!!!! " + doc.attr("href") + " !!!!!!!!!!!!!!!!");
        } catch (IOException ex2) {
            ex2.printStackTrace();
            error = true;
        } finally {
            if (!error && connected) {
                Elements links = doc.select("a[href]");
                for (int i = 0; i < links.size(); ++i) {
                    if (!isBrokenLink(links.get(i)) && !isVisited(links.get(i))) {
                        if (isProperPage(links.get(i))) {
                            visitedPages.add(links.get(i));
                            uniquePagesNum += 1;
                            System.out.println(uniquePagesNum + " : " + links.get(i).attr("href"));
                            findBroken(links.get(i));
                        }
                    }
                }
            }
        }
    }

    private static boolean isVisited(Element e) {
        for (int i = 0; i < visitedPages.size(); ++i) {
            if (visitedPages.get(i).attr("href").equals(e.attr("href")))
                return true;
        }
        return false;
    }

    private static boolean isBrokenLink(Element e) {
        return brokenPages.contains(e);
    }

    private static boolean isProperPage(Element e) {
        return e.attr("href").contains("hse.ru");
    }
}