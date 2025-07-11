package com.sydh.common.enums;

public enum TranslateType {
    MENU_TYPE("menu", "sys_menu_translate", "sys_menu", "menu_id", "menu_name"),
    DICT_DATA_TYPE("dict_data", "sys_dict_data_translate", "sys_dict_data", "dict_code", "dict_label"),
    DICT_TYPE_TYPE("dict_type", "sys_dict_type_translate", "sys_dict_type", "dict_id", "dict_name"),
    THINGS_MODEL_TYPE("things_model", "iot_things_model_translate", "iot_things_model", "model_id", "model_name"),
    THINGS_MODEL_TEMPLATE_TYPE("things_model_template", "iot_things_model_template_translate", "iot_things_model_template", "template_id", "template_name");

    private final String value;
    private final String translateTable;
    private final String sourceTable;
    private final String idColumn;
    private final String nameColumn;

    TranslateType(String value, String translateTable, String sourceTable, String idColumn, String nameColumn) {
        this.value = value;
        this.translateTable = translateTable;
        this.sourceTable = sourceTable;
        this.idColumn = idColumn;
        this.nameColumn = nameColumn;
    }

    public String getValue() {
        return value;
    }

    public String getTranslateTable() {
        return translateTable;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public String getNameColumn() {
        return nameColumn;
    }
}
