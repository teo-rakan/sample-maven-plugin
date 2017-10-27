package com.epam.jira;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.util.List;

public class TestManagementMojo extends AbstractMojo {

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        boolean hasTestNG = containsDependencyById("testng");
        boolean hasJunit = containsDependencyById("junit");

        Plugin surefirePlugin = lookupPlugin("org.apache.maven.plugins:maven-surefire-plugin");

        //todo add if null ? project.addPlugin();

        Object config = updateConfiguration(hasJunit, hasTestNG, surefirePlugin.getConfiguration());
        surefirePlugin.setConfiguration(config);

    }

    private Plugin lookupPlugin(String pluginName) {
        List<Plugin> plugins = project.getBuildPlugins();

        for (Plugin plugin : plugins)
            if (pluginName.equalsIgnoreCase(plugin.getKey()))
                return plugin;

        return null;
    }

    private boolean containsDependencyById(String artifactId) {
        List<Dependency> dependencies = project.getDependencies();

        for (Dependency dependency : dependencies)
            if (dependency.getArtifactId().equalsIgnoreCase(artifactId))
                return true;

        return false;
    }

    private Object updateConfiguration(boolean hasJunit, boolean hasTestNG, Object configuration) {

        if (!hasJunit && !hasTestNG) return null;

        if (configuration == null)
            configuration = new Xpp3Dom("configuration");

        if (configuration instanceof Xpp3Dom) {

            Xpp3Dom xml = (Xpp3Dom) configuration;
            Xpp3Dom properties = xml.getChild("properties");
            if (properties == null) {
                properties = new Xpp3Dom("properties");
                xml.addChild(properties);
            }
            Xpp3Dom[] propertyList = properties.getChildren("property");

            //mine
            Xpp3Dom listenerProperty = null;
            for (Xpp3Dom property : propertyList) {
                if (property.getChild("name").getValue().equalsIgnoreCase("listener")) {
                    listenerProperty = property;
                    break;
                }
            }
            if (listenerProperty == null) {
                Xpp3Dom name = new Xpp3Dom("name");
                name.setValue("listener");

                listenerProperty = new Xpp3Dom("property");
                listenerProperty.addChild(name);
                listenerProperty.addChild(new Xpp3Dom("value"));
                properties.addChild(listenerProperty);
            }

            String newValue = listenerProperty.getChild("value").getValue()
                    + (hasTestNG ? "," + com.epam.jira.testng.ExecutionListener.class.getName() : "")
                    + (hasJunit ? "," + com.epam.jira.junit.ExecutionListener.class.getName() : "" );
            listenerProperty.setValue(newValue);
            //todo check if properties addChild is needed

        }
        return configuration;
    }
}
