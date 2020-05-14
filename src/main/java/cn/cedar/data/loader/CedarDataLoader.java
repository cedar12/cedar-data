package cn.cedar.data.loader;

import cn.cedar.data.CedarDataBase;
import cn.cedar.data.expcetion.NotFoundDynamicMethodSqlException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cedar12.zxd@qq.com
 */
public class CedarDataLoader extends CedarDataBase {

	public static List<String> load(String path,Class<?> cls){
        if(!path.startsWith(FILE_SEPARATOR)){
            path =FILE_SEPARATOR + path;
        }
        if(!path.endsWith(FILE_SUFFIX)){
            path=path+FILE_SUFFIX;
        }
        if(!path.startsWith(FILE_SEPARATOR)){
            path=FILE_SEPARATOR+path;
        }
        List<String> list=new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(path)));
            String line = "";
            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(line);
            }
        }catch (NullPointerException e){
            throw new NotFoundDynamicMethodSqlException(path);
        }
        return list;
    }
	
}
