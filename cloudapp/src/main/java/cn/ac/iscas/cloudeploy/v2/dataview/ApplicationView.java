package cn.ac.iscas.cloudeploy.v2.dataview;

import java.util.Date;
import java.util.List;

import javax.transaction.HeuristicCommitException;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;

public class ApplicationView {
	public static class Item {
		public Long id;
		public String name;
		public Application.Status status;
		public Long createdAt;
		public Long updatedAt;
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if (obj instanceof Item) {
	            return this.id.equals(((Item)obj).id);
	        }
			return  false;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Application.Status getStatus() {
			return status;
		}
		public void setStatus(Application.Status status) {
			this.status = status;
		}
		public Long getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(Long createdAt) {
			this.createdAt = createdAt;
		}
		public Long getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(Long updatedAt) {
			this.updatedAt = updatedAt;
		}
	}
	
	public static class DetailedItem extends Item {
		public List<ContainerView.DetailedItem> containers;
		public List<RelationView.Item> relations;
		public List<ContainerView.DetailedItem> getContainers() {
			return containers;
		}
		public void setContainers(List<ContainerView.DetailedItem> containers) {
			this.containers = containers;
		}
		public List<RelationView.Item> getRelations() {
			return relations;
		}
		public void setRelations(List<RelationView.Item> relations) {
			this.relations = relations;
		}
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if (obj instanceof DetailedItem) {
	            return this.id.equals(((Item)obj).id);
	        }
			return  false;
		}
		
	
	}

	private static Function<Application, Item> ITEM_VIEW_TRANSFORMER = new Function<Application, Item>() {
		@Override
		public Item apply(Application input) {
			
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.status = input.getStatus();
			view.createdAt = input.getCreateTime() == null ? null : input
					.getCreateTime().getTime();
			view.updatedAt = input.getUpdateTime() == null ? null : input
					.getUpdateTime().getTime();
			return view;
		}
	};
	
	public static Item viewOf(Application input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Application> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}
	//Application获取详细信息
	private static Function<Application, DetailedItem> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<Application, DetailedItem>() {
		@Override
		public DetailedItem apply(Application input) {
			if (input == null)
				return null;
			DetailedItem view = new DetailedItem();
			view.id = input.getId();
			view.name = input.getName();
			view.status = input.getStatus();
			view.createdAt = input.getCreateTime() == null ? new Date().getTime() : input
					.getCreateTime().getTime()+8*60*60*1000;
			view.updatedAt = input.getUpdateTime() == null ? new Date().getTime() : input
					.getUpdateTime().getTime()+8*60*60*1000;
			view.containers = ContainerView.detailedViewListOf(input
					.getContainers(),input);
			view.relations = RelationView.viewListOf(input.getRelations());
			return view;
		}
	};

	public static DetailedItem detailedViewOf(Application input) {
		return input == null ? null : DTAILED_ITEM_VIEW_TRANSFORMER
				.apply(input);
	}

	public static List<DetailedItem> detailedViewListOf(List<Application> input) {
		return input == null ? ImmutableList.<DetailedItem> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER);
	}
}
