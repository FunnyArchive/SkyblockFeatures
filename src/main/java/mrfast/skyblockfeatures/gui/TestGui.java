package mrfast.skyblockfeatures.gui;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import gg.essential.api.utils.GuiUtil;
import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.ScrollComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.UIWrappedText;
import gg.essential.elementa.components.inspector.Inspector;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.animation.AnimatingConstraints;
import gg.essential.elementa.constraints.animation.Animations;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.effects.RecursiveFadeEffect;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import gg.essential.vigilance.gui.common.input.UITextInput;
import gg.essential.vigilance.gui.settings.CheckboxComponent;
import gg.essential.vigilance.gui.settings.SelectorComponent;
import gg.essential.vigilance.gui.settings.SliderComponent;
import gg.essential.vigilance.gui.settings.SwitchComponent;
import gg.essential.vigilance.gui.settings.TextComponent;
import kotlin.Unit;
import mrfast.skyblockfeatures.skyblockfeatures;
import mrfast.skyblockfeatures.core.Config;
import mrfast.skyblockfeatures.utils.Utils;
public class TestGui extends WindowScreen {
    public static SortedMap<String, SortedMap<String,List<Property>>> catagories = new TreeMap<>();
    public static HashMap<Property, Object> valueMap = new HashMap<>();
    public static String selectedCatagory = "General";
    public String searchQuery = "";
    // @Override
    // public void OnG
    @Override
	public void onScreenClose() {
		skyblockfeatures.config.markDirty();
        skyblockfeatures.config.writeData();
	}

    Color clear = new Color(0,0,0,0);
    public TestGui(Boolean doAnimation) {
        super(ElementaVersion.V2);
        reloadAllCatagories();
        
        int screenWidth = Utils.GetMC().currentScreen.width;
        int screenHeight = Utils.GetMC().currentScreen.height;
        UIComponent box = new UIRoundedRectangle(15f)
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .setWidth(new PixelConstraint(0.70f*screenWidth))
            .setHeight(new PixelConstraint(0.70f*screenHeight))
            .setChildOf(getWindow())
            .setColor(new Color(25,25,25,200))
            .enableEffect(new ScissorEffect());

        // Animate, set color, etc.
        
        float guiWidth = box.getWidth();
        float guiHeight = box.getHeight();
        double fontScale = screenHeight/540d;
        
        UIComponent titleArea = new UIBlock().setColor(clear).setChildOf(getWindow())
            .setX(new CenterConstraint())
            .setWidth(new PixelConstraint(guiWidth))
            .setHeight(new PixelConstraint(0.15f*guiHeight))
            .enableEffect(new ScissorEffect());
            
        UIComponent titleText = new UIText("Skyblock Features")
            .setColor(new Color(0x00FFFF))
            .setChildOf(titleArea)
            .setX(new CenterConstraint())
            .setY(new CenterConstraint())
            .enableEffect(new ScissorEffect())
            .setTextScale(new PixelConstraint((float) (doAnimation?1*fontScale:4*fontScale)));

        new Inspector(getWindow()).setChildOf(getWindow());
        
        UIComponent searchBox = new UIBlock()
            .setChildOf(titleArea)
            .setX(new PixelConstraint(guiWidth-90))
            .setY(new CenterConstraint())
            .setWidth(new PixelConstraint(80))
            .setColor(new Color(120,120,120,60))
            .setHeight(new PixelConstraint(15f));

        UITextInput input = (UITextInput) new UITextInput("Search...")
            .setChildOf(searchBox)
            .setX(new PixelConstraint(5f))
            .setWidth(new PixelConstraint(80))
            .setHeight(new PixelConstraint(15f))
            .setY(new PixelConstraint(3f));
        
        titleArea.onMouseClickConsumer((event)->{
            input.grabWindowFocus();;
        });
        
        // Gray horizontal line 1px from bottom of the title area
        new UIBlock().setChildOf(titleArea)
            .setWidth(new PixelConstraint(guiWidth))
            .setHeight(new PixelConstraint(1f))
            .setX(new CenterConstraint())
            .setY(new PixelConstraint(titleArea.getHeight()-1))
            .setColor(new Color(0x808080));

        // Area of where the currently selected catagorie's feature will be displayed
        UIComponent loadedFeaturesList = new ScrollComponent("No Matching Settings Found", 10f, new Color(0xa9a9a9), false, true, false, false, 25f, 1f, null)
            .setX(new PixelConstraint(0.25f*guiWidth))
            .setY(new PixelConstraint(titleArea.getHeight()))
            .setColor(Color.red)
            .enableEffect(new ScissorEffect())
            .setWidth(new PixelConstraint(0.75f*guiWidth))
            .setHeight(new PixelConstraint((0.85f*guiHeight)));
        loadedFeaturesList.clearChildren();
        reloadFeatures(loadedFeaturesList,guiHeight,guiWidth,fontScale);

        input.onKeyType((component, character, integer) -> {
            searchQuery = ((UITextInput) component).getText().toLowerCase();
            loadedFeaturesList.clearChildren();
            reloadFeatures(loadedFeaturesList,guiHeight,guiWidth,fontScale);
            return Unit.INSTANCE;
        });
        
        // Side bar on the left that holds the catagories
        UIComponent sidebarArea = new UIBlock()
            .setX(new PixelConstraint(0f))
            .setY(new PixelConstraint(titleArea.getHeight()))
            .setWidth(new PixelConstraint(0.25f*guiWidth))
            .setHeight(new PixelConstraint(0.85f*guiHeight))
            .setChildOf(getWindow())
            .setColor(clear)
            .enableEffect(new ScissorEffect());
        // Seperator to the right side of the sidebar
        UIComponent sidebarSeperator = new UIBlock()
            .setWidth(new PixelConstraint(1f))
            .setHeight(new PixelConstraint(0.85f*guiHeight))
            .setX(new PixelConstraint(0.25f*guiWidth))
            .setY(new PixelConstraint(titleArea.getHeight()))
            .setColor(new Color(0x808080));
        int Index = 0;

        // Draw catagorys on sidebar
        for(String catagoryName:catagories.keySet()) {
            UIComponent ExampleCatagory = new UIText(Utils.capitalizeString(catagoryName))
                .setChildOf(sidebarArea)
                .setColor(new Color(0xFFFFFF))
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(10f+(Index*20)))
                .enableEffect(new RecursiveFadeEffect())
                .setTextScale(new PixelConstraint((float) fontScale*2));
            ExampleCatagory.onMouseEnterRunnable(()->{
                ExampleCatagory.setColor(new Color(0x52cbff));
            });
            ExampleCatagory.onMouseLeaveRunnable(()->{
                if(catagoryName!=selectedCatagory) ExampleCatagory.setColor(new Color(0xFFFFFF));
            });
            ExampleCatagory.onMouseClickConsumer((event)->{
                selectedCatagory = catagoryName;
                LoadCatagory(catagoryName);
            });
            Index++;
        }

        UIComponent editGuiButton = new UIRoundedRectangle(10f).setColor(new Color(0,0,0,50))
            .setX(new PixelConstraint(0.15f*0.25f*guiWidth))
            .setY(new PixelConstraint(0.90f*guiHeight))
            .setHeight(new PixelConstraint(0.85f*0.10f*guiHeight))
            .setWidth(new PixelConstraint(0.70f*0.25f*guiWidth))
            .setChildOf(sidebarArea);
        new UIText("Edit Gui Locations").setColor(Color.white).setChildOf(editGuiButton)
            .setTextScale(new PixelConstraint((float) fontScale))
            .setX(new CenterConstraint())
            .setY(new CenterConstraint());

        editGuiButton.onMouseEnterRunnable(()->{
            editGuiButton.setColor(new Color(0,0,0,75));
        });
        editGuiButton.onMouseLeaveRunnable(()->{
            editGuiButton.setColor(new Color(0,0,0,50));
        });
        // Open gui locations gui when clicked
        editGuiButton.onMouseClickConsumer((event)->{
            GuiUtil.open(new LocationEditGui());
        });
        box.addChild(titleArea);
        box.addChild(sidebarArea);
        box.addChild(sidebarSeperator);
        box.addChild(loadedFeaturesList);
        box.addChild(editGuiButton);
        if(doAnimation) {
            box.setWidth(new PixelConstraint(0f));

            AnimatingConstraints anim = box.makeAnimation();
            anim.setWidthAnimation(Animations.OUT_EXP, 0.5f, new PixelConstraint(0.70f*screenWidth));
            box.animateTo(anim);

            AnimatingConstraints animation = titleText.makeAnimation();
            animation.setTextScaleAnimation(Animations.OUT_EXP, 0.5f, new PixelConstraint((float) (4.0*fontScale)));
            titleText.animateTo(animation);
        }
        
    }

    public void reloadAllCatagories() {
        catagories.clear();
        Config field = skyblockfeatures.config;
        Field[] fieldsOfFieldClass = Config.class.getFields();
        for(int i = 0;i < fieldsOfFieldClass.length; i++) {
            try {
                Object value = fieldsOfFieldClass[i].get(field);
                if (fieldsOfFieldClass[i].isAnnotationPresent(Property.class)) {
                    Property feature = fieldsOfFieldClass[i].getAnnotation(Property.class);
                    // Create catagory if not exist already
                    if(!catagories.containsKey(feature.category())) {
                        catagories.put(feature.category(), new TreeMap<>());
                    }
                    SortedMap<String, List<Property>> catagory = catagories.get(feature.category());

                    // Create subcatagory if not exist already
                    if(!catagory.containsKey(feature.subcategory())) {
                        catagory.put(feature.subcategory(), new ArrayList<>());
                    }
                    List<Property> subcatagory = catagory.get(feature.subcategory());
            
                    if(!subcatagory.contains(feature)) {
                        valueMap.put(feature, value);
                        subcatagory.add(feature);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void reloadFeatures(UIComponent loadedFeaturesList, float guiHeight, float guiWidth, double fontScale) {
        int index = 0; 
        // Default catagory
        for(String catagoryName:catagories.keySet()) {
            if(searchQuery.isEmpty()) {
                if(!catagoryName.equals(selectedCatagory)) {
                    continue;
                }
            }
            for(String subcatagoryName:catagories.get(catagoryName).keySet()) {
                List<Property> subcatagory = catagories.get(catagoryName).get(subcatagoryName);
                int featuresVisible = 0;
                for(Property feature:subcatagory) {
                    if((!feature.name().toLowerCase().contains(searchQuery) && !feature.description().toLowerCase().contains(searchQuery)) || feature.hidden()) {
                        continue;
                    }
                    featuresVisible++;
                }
                // Dont show subcatagory names if no elements of it are visible
                if(featuresVisible==0) continue;

                // Render subcatagory name
                new UIText(subcatagoryName).setChildOf(loadedFeaturesList)
                        .setY(new PixelConstraint(15+((5+0.15f*0.85f*guiHeight)*index)-20))
                        .setX(new CenterConstraint())
                        .setTextScale(new PixelConstraint((float) fontScale*3));
                index++;
                for(Property feature:subcatagory) {
                    if((!feature.name().toLowerCase().contains(searchQuery) && !feature.description().toLowerCase().contains(searchQuery)) || feature.hidden()) {
                        continue;
                    }
                    
                    // allFeatures
                    UIComponent exampleFeature = new UIBlock().setChildOf(loadedFeaturesList).setColor(new Color(0,0,0,150))
                        .setX(new CenterConstraint())
                        .setY(new PixelConstraint(((5+0.15f*0.85f*guiHeight)*index)-20))
                        .setWidth(new PixelConstraint(0.90f*0.75f*guiWidth))
                        .setHeight(new PixelConstraint(0.15f*0.85f*guiHeight))
                        .enableEffect(new OutlineEffect(new Color(0xa9a9a9),1f));
        
                    // Feature Title
                    new UIText(feature.name()).setChildOf(exampleFeature)
                        .setY(new PixelConstraint(4f))
                        .setX(new PixelConstraint(4f))
                        .setTextScale(new PixelConstraint((float) fontScale*2f));
        
                    if(feature.type() == PropertyType.PARAGRAPH) {
                        new UIWrappedText(feature.description()).setChildOf(exampleFeature)
                            .setX(new PixelConstraint(4f))
                            .setWidth(new PixelConstraint(200))
                            .setColor(new Color(187,187,187))
                            .setY(new PixelConstraint(23f*(float) fontScale))
                            .setTextScale(new PixelConstraint((float) fontScale*1f));
                    } else {
                        new UIWrappedText(feature.description()).setChildOf(exampleFeature)
                            .setX(new PixelConstraint(4f))
                            .setWidth(new PixelConstraint(350))
                            .setColor(new Color(187,187,187))
                            .setY(new PixelConstraint(23f*(float) fontScale))
                            .setTextScale(new PixelConstraint((float) fontScale*1f));
                    }
                    
        
                    if(feature.type() == PropertyType.SWITCH) {
                        UIComponent comp = new SwitchComponent((Boolean) valueMap.get(feature)).setChildOf(exampleFeature);
                        comp.onMouseClickConsumer((event)->{
                            Boolean val = (Boolean) getVariable(feature.name());
                            setVariable(feature.name(),!val);
                        });
                    }
        
                    if(feature.type() == PropertyType.CHECKBOX) {
                        UIComponent comp = new CheckboxComponent((Boolean) valueMap.get(feature)).setChildOf(exampleFeature);
                        comp.onMouseClickConsumer((event)->{
                            Boolean val = (Boolean) getVariable(feature.name());
                            setVariable(feature.name(),!val);
                        });
                    }
        
                    if(feature.type() == PropertyType.SELECTOR) {
                        UIComponent comp = new SelectorComponent((int) valueMap.get(feature),getOptions(feature.name())).setChildOf(exampleFeature);
                        ((SelectorComponent) comp).onValueChange((value)->{
                            setVariable(feature.name(),value);
                            return Unit.INSTANCE;
                        });
                    }

                    if(feature.type() == PropertyType.TEXT) {
                        UIComponent comp = new TextComponent((String) valueMap.get(feature), "", false, false).setChildOf(exampleFeature);
                        if(feature.name().contains("API")) {
                            comp = new TextComponent((String) valueMap.get(feature), "", false, true).setChildOf(exampleFeature);
                        }
                        ((TextComponent) comp).onValueChange((value)->{
                            setVariable(feature.name(),value);
                            return Unit.INSTANCE;
                        });
                    }

                    if(feature.type() == PropertyType.PARAGRAPH) {
                        UIComponent comp = new TextComponent((String) valueMap.get(feature), "", true, false).setChildOf(exampleFeature);
                        ((TextComponent) comp).onValueChange((value)->{
                            setVariable(feature.name(),value);
                            return Unit.INSTANCE;
                        });
                    }

                    if(feature.type() == PropertyType.SLIDER) {
                        UIComponent comp = new SliderComponent((Integer) valueMap.get(feature), feature.min(), feature.max()).setChildOf(exampleFeature);
                        ((SliderComponent) comp).onValueChange((value)->{
                            setVariable(feature.name(),value);
                            return Unit.INSTANCE;
                        });
                    }
                    index++;
                }
            }
        }
    }

    public void setVariable(String name,Object newValue) {
        Config field = skyblockfeatures.config;
        Field[] fieldsOfFieldClass = Config.class.getFields();
        for(int i = 0;i < fieldsOfFieldClass.length; i++) {
            try {
                if (fieldsOfFieldClass[i].isAnnotationPresent(Property.class)) {
                    Property featureProperty = fieldsOfFieldClass[i].getAnnotation(Property.class);
                    if(featureProperty.name()==name) {
                        fieldsOfFieldClass[i].setAccessible(true);
                        fieldsOfFieldClass[i].set(field, newValue);
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        reloadAllCatagories();
    }

    public Object getVariable(String name) {
        Config field = skyblockfeatures.config;
        Field[] fieldsOfFieldClass = Config.class.getFields();
        for(int i = 0;i < fieldsOfFieldClass.length; i++) {
            try {
                Object value = fieldsOfFieldClass[i].get(field);
                if (fieldsOfFieldClass[i].isAnnotationPresent(Property.class)) {
                    Property featureProperty = fieldsOfFieldClass[i].getAnnotation(Property.class);
                    if(featureProperty.name()==name) {
                        return value;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return null;
    }

    public ArrayList getOptions(String name) {
        Field[] fieldsOfFieldClass = Config.class.getFields();
        for(int i = 0;i < fieldsOfFieldClass.length; i++) {
            try {
                if (fieldsOfFieldClass[i].isAnnotationPresent(Property.class)) {
                    Property featureProperty = fieldsOfFieldClass[i].getAnnotation(Property.class);
                    if(featureProperty.name()==name) {
                        return new ArrayList(Arrays.asList(featureProperty.options()));
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return null;
    }

    public void LoadCatagory(String catagoryName) {
        GuiUtil.open(new TestGui(false));
    }
}