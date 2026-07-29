package ru.otus.hw.finance_service.telegram.keyboard;

public interface Constants {

    // Category
    String CATEGORY_CALLBACK_PREFIX = "CATEGORY:";

    /**
     * Callback-команда просмотра категорий расходов.
     */
    String EXPENSE_CATEGORIES_CALLBACK = "CATEGORIES:EXPENSE";

    /**
     * Callback-команда просмотра категорий доходов.
     */
    String INCOME_CATEGORIES_CALLBACK = "CATEGORIES:INCOME";

    /**
     * Префикс callback-команды выбора категории для управления.
     */
    String MANAGE_CATEGORY_CALLBACK_PREFIX = "MANAGE_CATEGORY:";

    /**
     * Префикс callback-команды создания категории.
     */
    String ADD_CATEGORY_CALLBACK_PREFIX = "ADD_CATEGORY:";

    /**
     * Префикс callback-команды переименования категории.
     */
    String RENAME_CATEGORY_CALLBACK_PREFIX = "RENAME_CATEGORY:";

    /**
     * Префикс callback-команды удаления категории.
     */
    String DELETE_CATEGORY_CALLBACK_PREFIX = "DELETE_CATEGORY:";


    /**
     * Callback-команда статистики за текущий день.
     */
    String TODAY_STATISTICS_CALLBACK = "STATISTICS:TODAY";

    /**
     * Callback-команда статистики за текущий месяц.
     */
    String CURRENT_MONTH_STATISTICS_CALLBACK = "STATISTICS:CURRENT_MONTH";


    /**
     * Префикс callback-команды редактирования операции.
     */
    String EDIT_OPERATION_CALLBACK_PREFIX = "OPERATION_EDIT:";

    /**
     * Префикс callback-команды удаления операции.
     */
    String DELETE_OPERATION_CALLBACK_PREFIX = "OPERATION_DELETE:";


    /**
     * Префикс callback-команды выбора категории бюджета.
     */
    String BUDGET_CATEGORY_CALLBACK_PREFIX = "BUDGET_CATEGORY:";

    /**
     * Callback-команда просмотра бюджетов текущего месяца.
     */
    String CURRENT_BUDGETS_CALLBACK = "BUDGETS:CURRENT";

    /**
     * Callback-команда начала установки бюджета.
     */
    String SET_BUDGET_CALLBACK = "BUDGETS:SET";

    /**
     * Префикс callback-команды изменения бюджета.
     */
    String EDIT_BUDGET_CALLBACK_PREFIX = "BUDGET_EDIT:";

    /**
     * Префикс callback-команды удаления бюджета.
     */
    String DELETE_BUDGET_CALLBACK_PREFIX = "BUDGET_DELETE:";

    /**
     * Текст кнопки добавления расхода.
     */
    String ADD_EXPENSE_BUTTON = "➖ Расход";

    /**
     * Текст кнопки добавления дохода.
     */
    String ADD_INCOME_BUTTON = "➕ Доход";

    /**
     * Текст кнопки просмотра статистики.
     */
    String STATISTICS_BUTTON = "📊 Статистика";

    /**
     * Текст кнопки формирования отчёта.
     */
    String REPORT_BUTTON = "📄 Отчёт";

    /**
     * Текст кнопки управления категориями.
     */
    String CATEGORIES_BUTTON = "⚙️ Категории";

    /**
     * Текст кнопки просмотра финансовых операций.
     */
    String OPERATIONS_BUTTON = "📋 Мои операции";

    /**
     * Текст кнопки управления бюджетами.
     */
    String BUDGETS_BUTTON = "💰 Бюджеты";

    /**
     * Callback-команда выбора отчёта за текущий день.
     */
    String TODAY_REPORT_PERIOD_CALLBACK = "REPORT_PERIOD:TODAY";

    /**
     * Callback-команда выбора отчёта за текущий месяц.
     */
    String CURRENT_MONTH_REPORT_PERIOD_CALLBACK = "REPORT_PERIOD:CURRENT_MONTH";

    /**
     * Префикс callback-команды формирования DOCX-отчёта.
     */
    String DOCX_REPORT_CALLBACK_PREFIX = "REPORT_DOCX:";

    String  OPERATIONS_PAGE = "operations_page:";

}
