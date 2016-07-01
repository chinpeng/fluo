/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.fluo.api.config;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.configuration.ConfigurationConverter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link FluoConfiguration}
 */
public class FluoConfigurationTest {

  private FluoConfiguration base = new FluoConfiguration();

  @Test
  public void testDefaults() {
    Assert.assertEquals(FluoConfiguration.CLIENT_ZOOKEEPER_CONNECT_DEFAULT,
        base.getInstanceZookeepers());
    Assert.assertEquals(FluoConfiguration.CLIENT_ZOOKEEPER_TIMEOUT_DEFAULT,
        base.getZookeeperTimeout());
    Assert.assertEquals(FluoConfiguration.CLIENT_RETRY_TIMEOUT_MS_DEFAULT,
        base.getClientRetryTimeout());
    Assert.assertEquals(FluoConfiguration.CLIENT_ACCUMULO_ZOOKEEPERS_DEFAULT,
        base.getAccumuloZookeepers());
    Assert.assertEquals(FluoConfiguration.ADMIN_ACCUMULO_CLASSPATH_DEFAULT,
        base.getAccumuloClasspath());
    Assert.assertEquals(FluoConfiguration.WORKER_NUM_THREADS_DEFAULT, base.getWorkerThreads());
    Assert.assertEquals(FluoConfiguration.WORKER_INSTANCES_DEFAULT, base.getWorkerInstances());
    Assert.assertEquals(FluoConfiguration.WORKER_MAX_MEMORY_MB_DEFAULT, base.getWorkerMaxMemory());
    Assert.assertEquals(FluoConfiguration.WORKER_NUM_CORES_DEFAULT, base.getWorkerNumCores());
    Assert.assertEquals(FluoConfiguration.TRANSACTION_ROLLBACK_TIME_DEFAULT,
        base.getTransactionRollbackTime());
    Assert.assertEquals(FluoConfiguration.LOADER_NUM_THREADS_DEFAULT, base.getLoaderThreads());
    Assert.assertEquals(FluoConfiguration.LOADER_QUEUE_SIZE_DEFAULT, base.getLoaderQueueSize());
    Assert.assertEquals(FluoConfiguration.ORACLE_INSTANCES_DEFAULT, base.getOracleInstances());
    Assert.assertEquals(FluoConfiguration.ORACLE_MAX_MEMORY_MB_DEFAULT, base.getOracleMaxMemory());
    Assert.assertEquals(FluoConfiguration.ORACLE_NUM_CORES_DEFAULT, base.getOracleNumCores());
    Assert.assertEquals(FluoConfiguration.MINI_START_ACCUMULO_DEFAULT, base.getMiniStartAccumulo());
    Assert.assertTrue(base.getMiniDataDir().endsWith("/mini"));
  }

  @Test(expected = NoSuchElementException.class)
  public void testInstance() {
    base.getAccumuloInstance();
  }

  @Test(expected = NoSuchElementException.class)
  public void testUser() {
    base.getAccumuloUser();
  }

  @Test(expected = NoSuchElementException.class)
  public void testPassword() {
    base.getAccumuloPassword();
  }

  @Test(expected = NoSuchElementException.class)
  public void testTable() {
    base.getAccumuloTable();
  }

  @Test
  public void testSetGet() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertEquals("path1,path2", config.setAccumuloClasspath("path1,path2")
        .getAccumuloClasspath());
    Assert.assertEquals("instance", config.setAccumuloInstance("instance").getAccumuloInstance());
    Assert.assertEquals("pass", config.setAccumuloPassword("pass").getAccumuloPassword());
    Assert.assertEquals("table", config.setAccumuloTable("table").getAccumuloTable());
    Assert.assertEquals("user", config.setAccumuloUser("user").getAccumuloUser());
    Assert.assertEquals(4, config.setLoaderQueueSize(4).getLoaderQueueSize());
    Assert.assertEquals(0, config.setLoaderQueueSize(0).getLoaderQueueSize());
    Assert.assertEquals(7, config.setLoaderThreads(7).getLoaderThreads());
    Assert.assertEquals(0, config.setLoaderThreads(0).getLoaderThreads());
    Assert.assertEquals(8, config.setOracleMaxMemory(8).getOracleMaxMemory());
    Assert.assertEquals(10, config.setOracleInstances(10).getOracleInstances());
    Assert.assertEquals(11, config.setWorkerInstances(11).getWorkerInstances());
    Assert.assertEquals(12, config.setWorkerMaxMemory(12).getWorkerMaxMemory());
    Assert.assertEquals(13, config.setWorkerThreads(13).getWorkerThreads());
    Assert.assertEquals("zoos1", config.setInstanceZookeepers("zoos1").getInstanceZookeepers());
    Assert.assertEquals("zoos2", config.setAccumuloZookeepers("zoos2").getAccumuloZookeepers());
    Assert.assertEquals("app", config.setApplicationName("app").getApplicationName());
    Assert.assertEquals("zoos1/app", config.getAppZookeepers());
    Assert.assertEquals(14, config.setZookeeperTimeout(14).getZookeeperTimeout());
    Assert.assertEquals(15, config.setWorkerNumCores(15).getWorkerNumCores());
    Assert.assertEquals(16, config.setOracleNumCores(16).getOracleNumCores());
    Assert.assertFalse(config.setMiniStartAccumulo(false).getMiniStartAccumulo());
    Assert.assertEquals("mydata", config.setMiniDataDir("mydata").getMiniDataDir());
    Assert.assertEquals(17, config.setClientRetryTimeout(17).getClientRetryTimeout());
  }

  @Test
  public void testHasClientProps() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertFalse(config.hasRequiredClientProps());
    config.setApplicationName("app");
    Assert.assertFalse(config.hasRequiredClientProps());
    config.setAccumuloUser("user");
    Assert.assertFalse(config.hasRequiredClientProps());
    config.setAccumuloPassword("pass");
    Assert.assertFalse(config.hasRequiredClientProps());
    config.setAccumuloInstance("instance");
    Assert.assertTrue(config.hasRequiredClientProps());
  }

  @Test
  public void testHasAdminProps() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertFalse(config.hasRequiredAdminProps());
    config.setApplicationName("app");
    config.setAccumuloUser("user");
    config.setAccumuloPassword("pass");
    config.setAccumuloInstance("instance");
    config.setAccumuloTable("table");
    Assert.assertTrue(config.hasRequiredAdminProps());
  }

  @Test
  public void testHasWorkerProps() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertFalse(config.hasRequiredWorkerProps());
    config.setApplicationName("app");
    config.setAccumuloUser("user");
    config.setAccumuloPassword("pass");
    config.setAccumuloInstance("instance");
    Assert.assertTrue(config.hasRequiredWorkerProps());
  }

  @Test
  public void testHasOracleProps() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertFalse(config.hasRequiredOracleProps());
    config.setApplicationName("app");
    config.setAccumuloUser("user");
    config.setAccumuloPassword("pass");
    config.setAccumuloInstance("instance");
    Assert.assertTrue(config.hasRequiredOracleProps());
  }

  @Test
  public void testHasMiniFluoProps() {
    FluoConfiguration config = new FluoConfiguration();
    Assert.assertTrue(config.hasRequiredMiniFluoProps());
    config.setApplicationName("app");
    Assert.assertTrue(config.hasRequiredMiniFluoProps());
    config.setAccumuloUser("user");
    Assert.assertFalse(config.hasRequiredMiniFluoProps());
    config.setMiniStartAccumulo(false);
    Assert.assertFalse(config.hasRequiredMiniFluoProps());
    config.setAccumuloPassword("pass");
    config.setAccumuloInstance("instance");
    config.setAccumuloTable("table");
    Assert.assertTrue(config.hasRequiredMiniFluoProps());
  }

  @Test
  public void testLoadingPropsFile() {
    File propsFile = new File("../distribution/src/main/config/fluo.properties");
    Assert.assertTrue(propsFile.exists());

    FluoConfiguration config = new FluoConfiguration(propsFile);
    // make sure classpath contains comma. otherwise it was shortened
    Assert.assertTrue(config.getAccumuloClasspath().contains(","));
    // check for values set in prop file
    Assert.assertEquals("localhost/fluo", config.getInstanceZookeepers());
    Assert.assertEquals("localhost", config.getAccumuloZookeepers());
    Assert.assertEquals("", config.getAccumuloPassword());
    try {
      config.getAccumuloUser();
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      config.getAccumuloTable();
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      config.getAccumuloInstance();
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testMetricsProp() throws Exception {
    FluoConfiguration config = new FluoConfiguration();
    config.setProperty(FluoConfiguration.REPORTER_PREFIX + ".slf4j.logger", "m1");
    config.setProperty(FluoConfiguration.REPORTER_PREFIX + ".slf4j.frequency", "77");
    config.setProperty(FluoConfiguration.REPORTER_PREFIX + ".jmx.frequency", "33");

    Assert.assertEquals("m1", config.getReporterConfiguration("slf4j").getString("logger"));
    Assert.assertEquals("77", config.getReporterConfiguration("slf4j").getString("frequency"));
    Assert.assertEquals("33", config.getReporterConfiguration("jmx").getString("frequency"));
  }

  @Test
  public void testMultipleZookeepers() {
    Properties props = new Properties();
    props.setProperty(FluoConfiguration.CLIENT_ACCUMULO_ZOOKEEPERS_PROP, "zk1,zk2,zk3");
    props.setProperty(FluoConfiguration.CLIENT_ZOOKEEPER_CONNECT_PROP, "zk1,zk2,zk3/fluo");
    props.setProperty(FluoConfiguration.CLIENT_APPLICATION_NAME_PROP, "myapp");

    // ran into a bug where this particular constructor was truncating everything after zk1
    FluoConfiguration config =
        new FluoConfiguration(ConfigurationConverter.getConfiguration(props));
    Assert.assertEquals("zk1,zk2,zk3", config.getAccumuloZookeepers());
    Assert.assertEquals("zk1,zk2,zk3/fluo/myapp", config.getAppZookeepers());
  }

  private void assertIAE(String value) {
    FluoConfiguration config = new FluoConfiguration();
    try {
      config.setProperty(FluoConfiguration.OBSERVER_PREFIX + "1", value);
      config.getObserverConfig();
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testObserverConfig() {
    FluoConfiguration config = new FluoConfiguration();
    config.setProperty(FluoConfiguration.OBSERVER_PREFIX + "1",
        "com.foo.Observer2,configKey1=configVal1,configKey2=configVal2");
    List<ObserverConfiguration> ocList = config.getObserverConfig();
    Assert.assertEquals(1, ocList.size());
    Assert.assertEquals("com.foo.Observer2", ocList.get(0).getClassName());
    Assert.assertEquals("configVal1", ocList.get(0).getParameters().get("configKey1"));
    Assert.assertEquals("configVal2", ocList.get(0).getParameters().get("configKey2"));
    Assert.assertEquals(2, ocList.get(0).getParameters().size());
    assertIAE("class,bad,input");
    assertIAE("index,check,,phrasecount.PhraseCounter");
    assertIAE("");
    assertIAE(" ");
    assertIAE(",key=value");
    assertIAE(",");
    assertIAE("com.foo.Observer2,configKey1=,configKey2=configVal2");
    assertIAE("com.foo.Observer2,configKey1=val,=configVal2");

    config = new FluoConfiguration();
    config.setProperty(FluoConfiguration.OBSERVER_PREFIX + "1", "Class,");
    ocList = config.getObserverConfig();
    Assert.assertEquals(1, ocList.size());
    Assert.assertEquals("Class", ocList.get(0).getClassName());
    Assert.assertEquals(0, ocList.get(0).getParameters().size());
  }

  @Test
  public void testObserverConfig2() {
    FluoConfiguration config = new FluoConfiguration();

    ObserverConfiguration oc1 =
        new ObserverConfiguration("foo.class1").setParameters(ImmutableMap.of("param1", "a"));
    ObserverConfiguration oc2 =
        new ObserverConfiguration("foo.class2").setParameters(ImmutableMap.of("param1", "b"));
    ObserverConfiguration oc3 = new ObserverConfiguration("foo.class3");

    config.addObserver(oc1);
    config.addObserver(oc2);
    config.addObserver(oc3);

    Assert.assertEquals(ImmutableSet.of(oc1, oc2, oc3), new HashSet<>(config.getObserverConfig()));

    config.clearObservers();

    Assert.assertEquals(0, config.getObserverConfig().size());

    config.addObservers(Arrays.asList(oc1, oc2));

    Assert.assertEquals(ImmutableSet.of(oc1, oc2), new HashSet<>(config.getObserverConfig()));

    config.addObserver(oc3);

    Assert.assertEquals(ImmutableSet.of(oc1, oc2, oc3), new HashSet<>(config.getObserverConfig()));

    config.clearObservers();

    Assert.assertEquals(0, config.getObserverConfig().size());
  }

  private void assertSetNameIAE(String name) {
    FluoConfiguration config = new FluoConfiguration();
    try {
      config.setApplicationName(name);
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  private void assertGetNameIAE(String name) {
    FluoConfiguration config = new FluoConfiguration();
    try {
      config.setProperty(FluoConfiguration.CLIENT_APPLICATION_NAME_PROP, name);
      config.getApplicationName();
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testApplicationName() {
    FluoConfiguration config = new FluoConfiguration();
    config.setApplicationName("valid");
    Assert.assertEquals("valid", config.getApplicationName());
    String[] invalidNames = {"/name", "/", "te.t", ".", "", "a/b"};
    for (String name : invalidNames) {
      assertSetNameIAE(name);
      assertGetNameIAE(name);
    }
    assertSetNameIAE(null);
  }

  @Test
  public void testCopyConfig() {
    FluoConfiguration c1 = new FluoConfiguration();
    c1.setOracleNumCores(1);
    Assert.assertEquals(1, c1.getOracleNumCores());
    FluoConfiguration c2 = new FluoConfiguration(c1);
    Assert.assertEquals(1, c2.getOracleNumCores());
    c2.setOracleNumCores(2);
    Assert.assertEquals(2, c2.getOracleNumCores());
    Assert.assertEquals(1, c1.getOracleNumCores());
  }

  @Test
  public void testIAE() {
    FluoConfiguration config = new FluoConfiguration();
    String[] positiveIntMethods =
        {"setLoaderQueueSize", "setLoaderThreads", "setOracleInstances", "setOracleMaxMemory",
            "setOracleNumCores", "setWorkerInstances", "setWorkerMaxMemory", "setWorkerNumCores",
            "setWorkerThreads", "setZookeeperTimeout"};
    for (String methodName : positiveIntMethods) {
      try {
        config.getClass().getMethod(methodName, int.class).invoke(config, -5);
        Assert.fail();
      } catch (InvocationTargetException e) {
        if (!(e.getCause() instanceof IllegalArgumentException)) {
          Assert.fail();
        }
      } catch (Exception e) {
        e.printStackTrace();
        Assert.fail();
      }
    }
    String[] nonEmptyMethods =
        {"setAccumuloInstance", "setAccumuloTable", "setAccumuloUser", "setAccumuloZookeepers",
            "setMiniDataDir", "setInstanceZookeepers"};
    for (String methodName : nonEmptyMethods) {
      try {
        config.getClass().getMethod(methodName, String.class).invoke(config, "");
        Assert.fail();
      } catch (InvocationTargetException e) {
        if (!(e.getCause() instanceof IllegalArgumentException)) {
          Assert.fail();
        }
      } catch (Exception e) {
        e.printStackTrace();
        Assert.fail();
      }
    }
  }
}