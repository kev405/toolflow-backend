package com.codeflow.toolflow.util.enums;

public enum Role {

    ADMINISTRATOR,
    TOOL_ADMINISTRATOR,
    STUDENT,
    TEACHER;

    public String getEnumKey() {
        return this.name();
    }
}
