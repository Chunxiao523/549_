package com.calebdevelops.rajawalivrcardboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.Matrix;
import android.util.Log;

import com.calebdevelops.rawfinally.R;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.animation.Animation;
import rajawali.animation.SplineTranslateAnimation3D;
import rajawali.curves.CatmullRomCurve3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderOBJ;
import rajawali.renderer.RajawaliSideBySideRenderer;
import rajawali.terrain.SquareTerrain;
import rajawali.terrain.TerrainGenerator;

// this is for the res files

public class RajawaliVRExampleRenderer extends RajawaliVRRenderer {
	private SquareTerrain mTerrain;
	protected HeadTracker mHeadTracker;
	protected HeadTransform mHeadTransform;
	protected float[] mHeadViewMatrix;
	protected Matrix4 mHeadViewMatrix4;
	private float[] modelView =  new float[16];
	private float[] tempPosition = new float[16];
	private float[] headView = new float[16];
	private static final float[] POS_MATRIX_MULTIPLY_VEC = {0, 0, 0, 1.0f};
	private static final float YAW_LIMIT = 0.2f;
	private static final float PITCH_LIMIT = 0.2f;
	private Object3D  currentObj;
	private int count = 0;
	private int score = 0;
	LoaderOBJ hellfireobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.hellfire_obj);
	LoaderOBJ spaceshipobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.spaceship_obj);
	LoaderOBJ ewingobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.ewing_obj);
	public RajawaliVRExampleRenderer(Context context) {
		super(context);
		this.mHeadTracker = super.mHeadTracker;
		this.mHeadTransform = super.mHeadTransform;
		this.mHeadViewMatrix = super.mHeadViewMatrix;
		this.mHeadViewMatrix4 = super.mHeadViewMatrix4;
		//this.mCameraOrientation = new Quaternion();
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		mHeadTransform.getHeadView(headView, 0);
		if (isLookingAtObj(currentObj)){
			count ++;
			Log.v("is at center: ", Integer.toString(count));
			if (count >= 100) {
				getCurrentScene().removeChild(currentObj);
				setScore(10);
				Log.v("Object is removed: ", Integer.toString(count));
				setObject();
				setAnim();
				count = 0;
				Log.v("new object is added: ", Integer.toString(count));
			}
		} else {
			count = 0;
		}
	}

	@Override
	public void initScene() {
		try {
			hellfireobj.parse();
			spaceshipobj.parse();
			ewingobj.parse();
		} catch(Exception e) {
			e.printStackTrace();
		}
		DirectionalLight light = new DirectionalLight(0.2f, -1f, 0f);
		light.setPower(.7f);
		getCurrentScene().addLight(light);

		light = new DirectionalLight(0.2f, 1f, 0f);
		light.setPower(1f);
		getCurrentScene().addLight(light);

		getCurrentCamera().setFarPlane(1000);

		getCurrentScene().setBackgroundColor(0xdddddd);

		createTerrain();

		try {
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx, R.drawable.posy, R.drawable.negy, R.drawable.posz, R.drawable.negz);
			setObject();
			setAnim();

		//Canvas map = textAsBitmap("hello");
		//	getCurrentScene().addChild((Canvas)map);
			//X: right
			//Y: height
			//Z: back
		} catch(Exception e) {
			e.printStackTrace();
		}

		super.initScene();
	}

	public Canvas textAsBitmap(String text) {// For later usage
		Paint paint = new Paint();
		paint.setTextSize(16);
		paint.setColor(0x666666);
		paint.setUnderlineText(true);
		paint.setTextAlign(Paint.Align.CENTER);
		int width = (int) (paint.measureText(text) + 0.5f); // round
		float baseline = (int) (paint.ascent() + 0.5f);
		int height = (int) (baseline + paint.descent() + 0.5f);
		Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.drawText(text, 0, baseline, paint);
		return canvas;
	}

	public Object3D createObject(int n, Object3D object) throws TextureException {
		try {
			LoaderOBJ obj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.spaceship_obj);
			LoaderAWD awd = new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.dark_fighter);
			obj.parse();
			awd.parse();
			object = obj.getParsedObject();
			switch (n){//awd: dark_fighter space_cruiser capital
				//obj: hellfire ewing spaceship
				case 1:{
					obj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.hellfire_obj);
					break;
				}
				case 2:{
					obj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.ewing_obj);
				}
				case 3:{
					awd= new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.dark_fighter);
					Material darkFighterMaterial = new Material();
					darkFighterMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
					darkFighterMaterial.setColorInfluence(0);
					darkFighterMaterial.enableLighting(true);
					darkFighterMaterial.addTexture(new Texture("darkFighterTex", R.drawable.dark_fighter_6_color));
					object = awd.getParsedObject();
					object.setMaterial(darkFighterMaterial);
					break;
				}
				case 4:{
					awd= new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.space_cruiser);
					awd.parse();
					Material cruiserMaterial = new Material();
					cruiserMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
					cruiserMaterial.setColorInfluence(0);
					cruiserMaterial.enableLighting(true);
					cruiserMaterial.addTexture(new Texture("spaceCruiserTex", R.drawable.space_cruiser_4_color_1));
					object = awd.getParsedObject();
					object.setMaterial(cruiserMaterial);
					break;
				}
				default: break;
			}
			return object;
		}catch(Exception e) {
			e.printStackTrace();
		}

		return object;
	}
	private void setAnim() {
		CatmullRomCurve3D path = choosepath();
		SplineTranslateAnimation3D anim = new SplineTranslateAnimation3D(path);
		anim.setDurationMilliseconds(88000);
		anim.setRepeatMode(Animation.RepeatMode.INFINITE);
		anim.setOrientToPath(true);
		anim.setTransformable3D(currentObj);
		getCurrentScene().registerAnimation(anim);
		anim.play();
	}
	public void setObject() {
		int index =(int) ((Math.random()) * 4);
		Log.v("new object added,index ", Integer.toString(index));
		LoaderOBJ loaderobj;
		switch (index) {
			case 0:
				loaderobj = spaceshipobj;
				break;
			case 1:
				loaderobj = ewingobj;
				break;
			case 2:
				loaderobj = hellfireobj;
				break;
			default:
				loaderobj = spaceshipobj;
				break;
		}
		currentObj = loaderobj.getParsedObject();
		currentObj.setY(-2);//height
		currentObj.setX(1);//right is positive, left is negative
		currentObj.setRotY(90);
		currentObj.setZ(-3);//front is negative   back is positive
		currentObj.setScale(0.2);
		getCurrentScene().addChild(currentObj);
	}	public CatmullRomCurve3D choosepath(){
		int n =(int) ((Math.random()) * 4);
		CatmullRomCurve3D path = new CatmullRomCurve3D();
		switch (n){
			case 0:{
				path.addPoint(new Vector3(0, -5, -10));//points that object will go through
				path.addPoint(new Vector3(10, -5, 0));
				path.addPoint(new Vector3(0, -4, 8));
				path.addPoint(new Vector3(-16, -6, 0));
				break;
			}
			case 1:{
				path.addPoint(new Vector3(-10, 2, -30));//points that object will go through
				path.addPoint(new Vector3(-10, 0, -20));
				path.addPoint(new Vector3(-10, -2, -0));
				path.addPoint(new Vector3(5, -2, 20));
				path.addPoint(new Vector3(10, 0, 0));
				path.addPoint(new Vector3(10, -2, -10));
				path.addPoint(new Vector3(10, -5, -30));
				break;
			}
			case 2:{
				path.addPoint(new Vector3(-30, 2, 10));//points that object will go through
				path.addPoint(new Vector3(-20, 0, 10));
				path.addPoint(new Vector3(0, -2, 10));
				path.addPoint(new Vector3(20, -2, -5));
				path.addPoint(new Vector3(0, 0, 10));
				path.addPoint(new Vector3(-10, -2, 10));
				path.addPoint(new Vector3(-30, -5, 10));
				break;
			}
			default:{
				path.addPoint(new Vector3(0, -5, -10));//points that object will go through
				path.addPoint(new Vector3(10, -5, 0));
				path.addPoint(new Vector3(0, -4, 8));
				path.addPoint(new Vector3(-16, -6, 0));
				path.isClosedCurve(true);
				break;
			}
		}
		path.isClosedCurve(true);
		return path;
	}

	public void createTerrain() {
		//
		// -- Load a bitmap that represents the terrain. Its color values will
		//    be used to generate heights.
		//

		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.terrain1);

		try {
			SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
			// -- set terrain scale
			//terrainParams.setScale(4f, 54f, 4f); //for terrain.jpg

			terrainParams.setScale(4f, 110f, 4f);
			// -- the number of plane subdivisions
			terrainParams.setDivisions(128);
			// -- the number of times the textures should be repeated
			terrainParams.setTextureMult(4);
			//
			// -- Terrain colors can be set by manually specifying base, middle and
			//    top colors.
			//
			// --  terrainParams.setBasecolor(Color.argb(255, 0, 0, 0));
			//     terrainParams.setMiddleColor(Color.argb(255, 200, 200, 200));
			//     terrainParams.setUpColor(Color.argb(255, 0, 30, 0));
			//
			// -- However, for this example we'll use a bitmap
			//
			//terrainParams.setColorMapBitmap(bmp);
			terrainParams.setBasecolor(Color.argb(0, 0, 0, 0));
			terrainParams.setMiddleColor(Color.argb(122, 122, 122, 200));
			terrainParams.setUpColor(Color.argb(255, 255, 255, 0));
			terrainParams.setColorMapBitmap(bmp);

			//
			// -- create the terrain
			//
			mTerrain = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		// -- The bitmap won't be used anymore, so get rid of it.
		//
		bmp.recycle();

		//
		// -- A normal map material will give the terrain a bit more detail.
		//
		Material material = new Material();
		material.enableLighting(true);
		material.useVertexColors(true);
		material.setDiffuseMethod(new DiffuseMethod.Lambert());
		try {
			Texture groundTexture = new Texture("ground", R.drawable.ground);
			groundTexture.setInfluence(.5f);
			material.addTexture(groundTexture);
			material.addTexture(new NormalMapTexture("groundNormalMap", R.drawable.groundnor));
			material.setColorInfluence(0);
		} catch (TextureException e) {
			e.printStackTrace();
		}

		//
		// -- Blend the texture with the vertex colors
		//
		material.setColorInfluence(.5f);
		mTerrain.setY(-200);
		mTerrain.setMaterial(material);

		getCurrentScene().addChild(mTerrain);
	}


	private boolean isLookingAtObj(Object3D object3D) {

		//	Matrix.multiplyMM(modelView, 0, headView, 0, object3D.getScenePosition(), 0);
		Matrix.multiplyMV(tempPosition, 0, object3D.getModelViewMatrix().getFloatValues(), 0, POS_MATRIX_MULTIPLY_VEC, 0);
		float pitch = (float) Math.atan2(tempPosition[1], -tempPosition[2]);
		float yaw = (float) Math.atan2(tempPosition[0], -tempPosition[2]);
		Boolean bool = Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
		String result = Boolean.toString(bool);
		return bool;
	}
	public void setScore(int plus) {
		score += plus;
	}

	public Integer getScore() {
		return score;
	}
}

