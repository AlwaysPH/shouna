package com.qcws.shouna.dto.api;

import java.util.List;

import lombok.Data;

@Data
public class ItemDTO {
	private Integer id;
	private String title;
	private String image;
	private List<ItemDTO> items;
}
