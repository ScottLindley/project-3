package com.scottlindley.Bundle.GsonObjects;

import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.Place;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Created by Scott Lindley on 12/2/2016.
 */

public class CustomTweet extends Tweet {

    public CustomTweet(Coordinates coordinates, String createdAt, Object currentUserRetweet, TweetEntities entities, TweetEntities extendedEtities, Integer favoriteCount, boolean favorited, String filterLevel, long id, String idStr, String inReplyToScreenName, long inReplyToStatusId, String inReplyToStatusIdStr, long inReplyToUserId, String inReplyToUserIdStr, String lang, Place place, boolean possiblySensitive, Object scopes, long quotedStatusId, String quotedStatusIdStr, Tweet quotedStatus, int retweetCount, boolean retweeted, Tweet retweetedStatus, String source, String text, List<Integer> displayTextRange, boolean truncated, User user, boolean withheldCopyright, List<String> withheldInCountries, String withheldScope, Card card) {
        super(coordinates, createdAt, currentUserRetweet, entities, extendedEtities,
                favoriteCount, favorited, filterLevel, id, idStr, inReplyToScreenName,
                inReplyToStatusId, inReplyToStatusIdStr, inReplyToUserId,
                inReplyToUserIdStr, lang, place, possiblySensitive, scopes,
                quotedStatusId, quotedStatusIdStr, quotedStatus, retweetCount,
                retweeted, retweetedStatus, source, text, displayTextRange,
                truncated, user, withheldCopyright, withheldInCountries, withheldScope, card);
    }

    public String getText(){
        return text;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public User getUser(){
        return user;
    }



}
