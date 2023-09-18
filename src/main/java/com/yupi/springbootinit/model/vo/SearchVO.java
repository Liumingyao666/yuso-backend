package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import java.io.Serializable;
import java.util.List;

import com.yupi.springbootinit.model.entity.User;
import lombok.Data;

/**
 *
 * 聚合搜索视图
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private static final long serialVersionUID = 1L;


}
