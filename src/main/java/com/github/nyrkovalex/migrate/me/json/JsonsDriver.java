package com.github.nyrkovalex.migrate.me.json;

import com.github.nyrkovalex.migrate.me.json.Jsons.Driver;

class JsonsDriver implements Driver {
    private final String className;
    private final String jar;
    
    private JsonsDriver(String className, String jar) {
        this.className = className;
        this.jar = jar;
    }

    @Override
    public String className() {
        return className;
    }

    @Override
    public String jar() {
        return jar;
    }

}
