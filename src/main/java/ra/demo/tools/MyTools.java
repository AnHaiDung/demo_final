package ra.demo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyTools {
    @Tool(
            name = "get_all_products",
            description = "Lấy danh sách tất cả các sản phẩm"
    )
    public List<String> getProducts(){
        return List.of("Sản phẩm 1","Sản phẩm 2", "Sản phẩm 3");
    }

    @Tool(
            name = "get_product_by_id",
            description = "Lấy thông tin sản phẩm theo id"
    )
    public String getProductById(
            @ToolParam(
                    description = "Mã sản phẩm cần tìm",
                    required = true
            )
            String id){
        if(id.equals("SP001"))
            return "Sản phẩm tìm thấy SP001";
        else
            return "Không có sản phẩm nào có mã "+id;
    }
}
