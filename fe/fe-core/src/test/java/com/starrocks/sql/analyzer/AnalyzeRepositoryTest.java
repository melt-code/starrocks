package com.starrocks.sql.analyzer;

import com.google.common.collect.Lists;
import com.starrocks.alter.AlterTest;
import com.starrocks.analysis.Analyzer;
import com.starrocks.backup.Repository;
import com.starrocks.backup.SnapshotInfo;
import com.starrocks.backup.Status;
import com.starrocks.catalog.BrokerMgr;
import com.starrocks.catalog.FsBroker;
import com.starrocks.common.AnalysisException;
import com.starrocks.mysql.privilege.Auth;
import com.starrocks.service.FrontendOptions;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static com.starrocks.sql.analyzer.AnalyzeTestUtil.analyzeFail;
import static com.starrocks.sql.analyzer.AnalyzeTestUtil.analyzeSuccess;

public class AnalyzeRepositoryTest {

    private SnapshotInfo info;

    @Before
    public void setUp() {
        List<String> files = Lists.newArrayList();
        files.add("1.dat");
        files.add("1.hdr");
        files.add("1.idx");
        info = new SnapshotInfo(1, 2, 3, 4, 5, 6, 7, "/path/to/tablet/snapshot/", files);

        new MockUp<FrontendOptions>() {
            @Mock
            String getLocalHostAddress() {
                return "127.0.0.1";
            }
        };

        new MockUp<BrokerMgr>() {
            @Mock
            public FsBroker getBroker(String name, String host) throws AnalysisException {
                return new FsBroker("10.74.167.16", 8111);
            }

        };

        new MockUp<Repository>() {
            @Mock
            public Status initRepository(){
                return Status.OK;
            }
        };
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        AlterTest.beforeClass();
        AnalyzeTestUtil.init();
    }


    @Test
    public void testShowRepositories(){
        String sql = "SHOW REPOSITORIES;";
        analyzeSuccess(sql);
        new MockUp<Analyzer>(){

            @Mock
            public String getClusterName() {
                return "";
            }
        };
        analyzeFail(sql);
    }
}