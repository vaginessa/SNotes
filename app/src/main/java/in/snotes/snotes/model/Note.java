package in.snotes.snotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;

public class Note implements Parcelable {

    private String title;
    private String content;
    private boolean isLocked;
    private int colorOfNote;
    private long createdTimestamp;
    private boolean isStarred;
    private boolean isRemainderSet;
    private long remainderTime;
    private DocumentReference documentReference;

    private Note(String title, String content, boolean isLocked, int colorOfNote, long createdTimestamp, boolean isStarred, boolean isRemainderSet, long remainderTime, DocumentReference documentReference) {
        this.title = title;
        this.content = content;
        this.isLocked = isLocked;
        this.colorOfNote = colorOfNote;
        this.createdTimestamp = createdTimestamp;
        this.isStarred = isStarred;
        this.isRemainderSet = isRemainderSet;
        this.remainderTime = remainderTime;
        this.documentReference = documentReference;
    }


    // The getters and setters are only for the Firebase Firestore
    // Use the builder to build the note as its more convenient
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getColorOfNote() {
        return colorOfNote;
    }

    public void setColorOfNote(int colorOfNote) {
        this.colorOfNote = colorOfNote;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public boolean isRemainderSet() {
        return isRemainderSet;
    }

    public void setRemainderSet(boolean remainderSet) {
        isRemainderSet = remainderSet;
    }

    public long getRemainderTime() {
        return remainderTime;
    }

    public void setRemainderTime(long remainderTime) {
        this.remainderTime = remainderTime;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public static class Builder {
        private String title;
        private String content;
        private boolean isLocked;
        private int colorOfNote;
        private long createdTimestamp;
        private boolean isStarred;
        private boolean isRemainderSet;
        private long remainderTime;
        private DocumentReference documentReference;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder reference(DocumentReference reference) {
            this.documentReference = reference;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder isLocked(boolean isLocked) {
            this.isLocked = isLocked;
            return this;
        }

        public Builder colorOfNote(int colorOfNote) {
            this.colorOfNote = colorOfNote;
            return this;
        }

        public Builder createdTime(long timestamp) {
            this.createdTimestamp = timestamp;
            return this;
        }

        public Builder isStarred(boolean isStarred) {
            this.isStarred = isStarred;
            return this;
        }

        public Builder hasRemainder(boolean hasRemainder) {
            this.isRemainderSet = hasRemainder;
            return this;
        }

        public Builder remainderTime(long remainderTime) {
            this.remainderTime = remainderTime;
            return this;
        }

        public Note buildNote() {
            return new Note(title, content, isLocked, colorOfNote, createdTimestamp, isStarred, isRemainderSet, remainderTime, documentReference);
        }

    }


    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isLocked=" + isLocked +
                ", colorOfNote=" + colorOfNote +
                ", createdTimestamp=" + createdTimestamp +
                ", isStarred=" + isStarred +
                ", isRemainderSet=" + isRemainderSet +
                ", remainderTime=" + remainderTime +
                ", documentReference=" + documentReference +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.colorOfNote);
        dest.writeLong(this.createdTimestamp);
        dest.writeByte(this.isStarred ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRemainderSet ? (byte) 1 : (byte) 0);
        dest.writeLong(this.remainderTime);
    }

    protected Note(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.isLocked = in.readByte() != 0;
        this.colorOfNote = in.readInt();
        this.createdTimestamp = in.readLong();
        this.isStarred = in.readByte() != 0;
        this.isRemainderSet = in.readByte() != 0;
        this.remainderTime = in.readLong();
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
