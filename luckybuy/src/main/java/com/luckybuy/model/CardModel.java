package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/8/18.
 */
public class CardModel implements Serializable{


    private long timesid;
    private String cardtype;
    private String title;
    private String cardsn;
    private String cardpsw;

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardsn() {
        return cardsn;
    }

    public void setCardsn(String cardsn) {
        this.cardsn = cardsn;
    }

    public String getCardpsw() {
        return cardpsw;
    }

    public void setCardpsw(String cardpsw) {
        this.cardpsw = cardpsw;
    }
}
