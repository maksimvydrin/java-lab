package org.example.social_network.serialization;

import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;

import java.io.IOException; // ДОБАВЛЕН ИМПОРТ
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class SocialNetworkData implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int VERSION_1 = 1;
    public static final int CURRENT_VERSION = 2; // Перешли на v2

    private int version = CURRENT_VERSION;
    private transient int targetVersion = CURRENT_VERSION;

    private Profile[] profiles = new Profile[0];
    private Community[] communities = new Community[0];
    private DelProfile[] deletedProfiles = new DelProfile[0];
    private FriendShip[] friendships = new FriendShip[0];

    public SocialNetworkData() {
    }

    public SocialNetworkData(
            Profile[] profiles,
            Community[] communities,
            DelProfile[] deletedProfiles,
            FriendShip[] friendships
    ) {
        this.version = CURRENT_VERSION;
        this.profiles = profiles != null ? profiles : new Profile[0];
        this.communities = communities != null ? communities : new Community[0];
        this.deletedProfiles = deletedProfiles != null ? deletedProfiles : new DelProfile[0];
        this.friendships = friendships != null ? friendships : new FriendShip[0];
    }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    // ДОБАВЛЕН НЕДОСТАЮЩИЙ МЕТОД
    public void setTargetVersion(int targetVersion) { this.targetVersion = targetVersion; }

    public Profile[] getProfiles() { return profiles; }
    public void setProfiles(Profile[] profiles) { this.profiles = profiles; }

    public Community[] getCommunities() { return communities; }
    public void setCommunities(Community[] communities) { this.communities = communities; }

    public DelProfile[] getDeletedProfiles() { return deletedProfiles; }
    public void setDeletedProfiles(DelProfile[] deletedProfiles) { this.deletedProfiles = deletedProfiles; }

    public FriendShip[] getFriendships() { return friendships; }
    public void setFriendships(FriendShip[] friendships) { this.friendships = friendships; }

    // Кастомная сериализация для сохранения schema_version в заголовке
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(targetVersion);
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int fileVersion = in.readInt();
        in.defaultReadObject();
        this.version = fileVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialNetworkData that = (SocialNetworkData) o;
        return version == that.version
                && Arrays.equals(profiles, that.profiles)
                && Arrays.equals(communities, that.communities)
                && Arrays.equals(deletedProfiles, that.deletedProfiles)
                && Arrays.equals(friendships, that.friendships);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(version);
        result = 31 * result + Arrays.hashCode(profiles);
        result = 31 * result + Arrays.hashCode(communities);
        result = 31 * result + Arrays.hashCode(deletedProfiles);
        result = 31 * result + Arrays.hashCode(friendships);
        return result;
    }
}