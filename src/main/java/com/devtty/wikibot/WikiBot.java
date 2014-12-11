package com.devtty.wikibot;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author drenning
 */
public class WikiBot {
    
    private static final String WIKI_DOMAIN = "test";
    private static final String WIKI_PWD = "test";
    private static final String WIKI_USER = "test";
    private static final String WIKI_URL = "http://test/";
    private static final String BOT_ART = "WikiBot";
    
    private MediaWikiBot wBot;
    private Article botArticle;
    
    
    final static Logger logger = LoggerFactory.getLogger(WikiBot.class);
    
    public WikiBot(){
        logger.info("RUUUN");
        wBot = new MediaWikiBot(WIKI_URL);
        wBot.login(WIKI_USER, WIKI_PWD, WIKI_DOMAIN);
        botArticle = wBot.getArticle(BOT_ART);
        SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        botArticle.setText("LAST RUN: " + sf.format(new Date()));
        //botArticle.save();
        
    }
    
    private void oldArt(){
        AllPageTitles apt =  new AllPageTitles(wBot);
        Date aYearAgo = new Date(System.currentTimeMillis() - (5*31536000000L));
        
        int i = 0;
        int j = 0;
        
        String tableContent = "\n\n{| class=\"wikitable sortable\"\n|- class=\"hintergrundfarbe 6\"\n| align=\"center\" style=\"background:#f0f0f0;\"|'''Artikel'''\n" +
                "| align=\"center\" style=\"background:#f0f0f0;\"|'''Bearbeiter'''\n" +
                "| align=\"center\" style=\"background:#f0f0f0;\"|'''Timestamp'''\n|-\n";
        
        for(String title : apt){
            //System.out.println(title);
            Article a = wBot.getArticle(title);
            i++;
            if(a.getEditTimestamp().before(aYearAgo)){
                j++;
                logger.info(a.getTitle() + " by " + a.getEditor() + " " + a.getEditTimestamp());
                tableContent = tableContent.concat("| [[" + a.getTitle() + "]]||" + a.getEditor() + "||" + a.getEditTimestamp() + " \n|- \n");
            }
        }
        
        tableContent = tableContent.concat("|}\n\nWe got " + i + " articles and " + j + " have to be reviewed!");
        
        logger.info("We got " + i + " articles and " + j + " have to be reviewed!");
        logger.info(tableContent);
               
        botArticle.setText(botArticle.getText() + tableContent);
        botArticle.save();
    }
    
    private void debug(){
        logger.debug(wBot.getWikiType());
        logger.debug("BASE: " + wBot.getSiteinfo().getBase());
        logger.debug("CASE: " + wBot.getSiteinfo().getCase());
        logger.debug("GENERATOR: " + wBot.getSiteinfo().getGenerator());
        logger.debug("MAINPAGE: " + wBot.getSiteinfo().getMainpage());
        logger.debug("SITENAME: " + wBot.getSiteinfo().getSitename());
        logger.debug("VERSION: " + wBot.getSiteinfo().getVersion().getNumber() + "_" + wBot.getSiteinfo().getVersion().getNumberVariation());
    }
    
    public static void main(String[] args) {
        
        WikiBot w = new WikiBot();        
        //Print Debug INFO
        w.debug();
        
        w.oldArt();

    }
    
}
