package com.devtty.wikibot;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
        CommandLine cl = null;
        HelpFormatter helpFmt = new HelpFormatter();
        CommandLineParser clParser = new BasicParser();
        
        Options clOptions = new Options();
        
        clOptions.addOption(OptionBuilder
                .withLongOpt("help")
                .withDescription("displays this help and exits")
                .isRequired(false)
                .create("h"));
        
      
        
        clOptions.addOption(OptionBuilder
                .withLongOpt("user")
                .withDescription("login name")
                .hasArg()
                .withArgName("LOGINNAME")
                .create());
        
        clOptions.addOption(OptionBuilder
                .withLongOpt("password")
                .withDescription("password")
                .hasArg()
                .withArgName("PASSWORD")
                .create());
        
        clOptions.addOption(OptionBuilder
                .withArgName("WIKI")
                .hasArg()
        .withDescription("url")
        .create("ur"));
        try{
            cl = clParser.parse(clOptions, args);
            if(cl.hasOption('h')){
                helpFmt.printHelp(WikiBot.class.getName(), clOptions);
                return;
            }
        }catch(ParseException pe){
            helpFmt.printHelp(WikiBot.class.getName(), clOptions);
            System.out.println("ParseException:" + pe.getMessage());
            return;
        }
        
        
      /*  WikiBot w = new WikiBot();        
        //Print Debug INFO
        w.debug();
        
        w.oldArt();
*/
    }
    
}
