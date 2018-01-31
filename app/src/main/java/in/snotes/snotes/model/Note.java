package in.snotes.snotes.model;

public class Note {

    private String title;
    private String content;
    private boolean isLocked;
    private String colorOfNote;
    private long createdTimestamp;
    private boolean isStarred;
    private boolean isRemainderSet;
    private long remainderTime;

    private Note(String title, String content, boolean isLocked, String colorOfNote, long createdTimestamp, boolean isStarred, boolean isRemainderSet, long remainderTime) {
        this.title = title;
        this.content = content;
        this.isLocked = isLocked;
        this.colorOfNote = colorOfNote;
        this.createdTimestamp = createdTimestamp;
        this.isStarred = isStarred;
        this.isRemainderSet = isRemainderSet;
        this.remainderTime = remainderTime;
    }

    public static class Builder {
        private String title;
        private String content;
        private boolean isLocked;
        private String colorOfNote;
        private long createdTimestamp;
        private boolean isStarred;
        private boolean isRemainderSet;
        private long remainderTime;

        public Builder title(String title) {
            this.title = title;
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

        public Builder colorOfNote(String colorOfNote) {
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
            return new Note(title, content, isLocked, colorOfNote, createdTimestamp, isStarred, isRemainderSet, remainderTime);
        }

    }


}
