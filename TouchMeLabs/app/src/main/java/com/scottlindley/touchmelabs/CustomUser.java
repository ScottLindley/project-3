package com.scottlindley.touchmelabs;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.models.UserEntities;

import java.util.List;

/**
 * Created by Scott Lindley on 12/2/2016.
 */

public class CustomUser extends User {
    public CustomUser(boolean contributorsEnabled, String createdAt, boolean defaultProfile, boolean defaultProfileImage, String description, String emailAddress, UserEntities entities, int favouritesCount, boolean followRequestSent, int followersCount, int friendsCount, boolean geoEnabled, long id, String idStr, boolean isTranslator, String lang, int listedCount, String location, String name, String profileBackgroundColor, String profileBackgroundImageUrl, String profileBackgroundImageUrlHttps, boolean profileBackgroundTile, String profileBannerUrl, String profileImageUrl, String profileImageUrlHttps, String profileLinkColor, String profileSidebarBorderColor, String profileSidebarFillColor, String profileTextColor, boolean profileUseBackgroundImage, boolean protectedUser, String screenName, boolean showAllInlineMedia, Tweet status, int statusesCount, String timeZone, String url, int utcOffset, boolean verified, List<String> withheldInCountries, String withheldScope) {
        super(contributorsEnabled, createdAt, defaultProfile, defaultProfileImage,
                description, emailAddress, entities, favouritesCount, followRequestSent,
                followersCount, friendsCount, geoEnabled, id, idStr, isTranslator, lang,
                listedCount, location, name, profileBackgroundColor, profileBackgroundImageUrl, profileBackgroundImageUrlHttps,
                profileBackgroundTile, profileBannerUrl, profileImageUrl, profileImageUrlHttps, profileLinkColor, profileSidebarBorderColor,
                profileSidebarFillColor, profileTextColor, profileUseBackgroundImage, protectedUser, screenName, showAllInlineMedia, status, statusesCount,
                timeZone, url, utcOffset, verified, withheldInCountries, withheldScope);
    }

    public String getName(){
        return name;
    }

    public String getScreenName(){
        return screenName;
    }
}
