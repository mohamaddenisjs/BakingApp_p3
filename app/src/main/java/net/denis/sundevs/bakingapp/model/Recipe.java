package net.denis.sundevs.bakingapp.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class Recipe{
  @SerializedName("image")
  @Expose
  private String image;
  @SerializedName("servings")
  @Expose
  private Integer servings;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("ingredients")
  @Expose
  private List<Ingredients> ingredients;
  @SerializedName("id")
  @Expose
  private Integer id;
  @SerializedName("steps")
  @Expose
  private List<Steps> steps;
  public void setImage(String image){
   this.image=image;
  }
  public String getImage(){
   return image;
  }
  public void setServings(Integer servings){
   this.servings=servings;
  }
  public Integer getServings(){
   return servings;
  }
  public void setName(String name){
   this.name=name;
  }
  public String getName(){
   return name;
  }
  public void setIngredients(List<Ingredients> ingredients){
   this.ingredients=ingredients;
  }
  public List<Ingredients> getIngredients(){
   return ingredients;
  }
  public void setId(Integer id){
   this.id=id;
  }
  public Integer getId(){
   return id;
  }
  public void setSteps(List<Steps> steps){
   this.steps=steps;
  }
  public List<Steps> getSteps(){
   return steps;
  }
}