package com.atomic.map;
import java.util.List;

interface IShizukuService {
    List<String> scanFiles(String path);
    boolean runAtomicSync(String src, String dst, in List<String> blacklist);
    void destroy() = 16777114;
}