package ru.otus.hw.template_service.renderer.impl;

import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.policy.DocumentRenderPolicy;
import org.springframework.stereotype.Component;
import ru.otus.hw.template_service.renderer.TemplateConfigurationFactory;

@Component
public class PoiTemplateConfigurationFactory implements TemplateConfigurationFactory {

    private static final String DYNAMIC_CONTENT_PLACEHOLDER = "dynamicContent";


    /**
     * {@inheritDoc}
     */
    @Override
    public Configure create() {
        LoopRowTableRenderPolicy loopPolicy = new LoopRowTableRenderPolicy(true);

        return Configure.builder()
                .bind("categoryExpenses", loopPolicy)
                .bind("operations", loopPolicy)
                .bind("budgets", loopPolicy)
                .bind(DYNAMIC_CONTENT_PLACEHOLDER, new DocumentRenderPolicy())
                .build();
    }
}