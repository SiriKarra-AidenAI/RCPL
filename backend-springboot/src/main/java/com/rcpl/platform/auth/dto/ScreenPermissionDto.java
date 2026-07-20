package com.rcpl.platform.auth.dto;

/** A screen's view/manage permission (mirrors ScreenPermission in the frontend contract). */
public record ScreenPermissionDto(boolean view, boolean manage) {
}
