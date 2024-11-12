package io.linkedlogics.js.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class JsContextFactory extends ContextFactory {
    @Override
    protected boolean hasFeature(Context cx, int featureIndex) {
        switch (featureIndex) {
            case Context.FEATURE_ENABLE_JAVA_MAP_ACCESS:
                return true;
            case Context.FEATURE_ENHANCED_JAVA_ACCESS:
            	return true;
        }
        return super.hasFeature(cx, featureIndex);
    }
}
