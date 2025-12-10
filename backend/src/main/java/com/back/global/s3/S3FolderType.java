package com.back.global.s3;

public enum S3FolderType {
	MEMBER_PROFILE_ORIGINAL("members/profile/originals/", false),
	MEMBER_PROFILE_THUMBNAIL("members/profile/resized/thumbnail/", true),
	POST_IMAGE_ORIGINAL("posts/images/originals/", false),
	POST_IMAGE_THUMBNAIL("posts/images/resized/thumbnail/", false),
	POST_IMAGE_DETAIL("posts/images/resized/detail/", false);

	private final String path;
	private final boolean deletable;

	S3FolderType(String path, boolean deletable) {
		this.path = path;
		this.deletable = deletable;
	}

	public String getPath() {
		return path;
	}

	public boolean isDeletable() {
		return deletable;
	}
}