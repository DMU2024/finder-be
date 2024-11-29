package dmu.finder.user.domain.repository;

public interface UserInfoMapping {
    long getUserId();

    String getUsername();

    String getProfileImage();

    String getThumbnailImage();

    boolean getNotifyOnlyBookmarked();
}
