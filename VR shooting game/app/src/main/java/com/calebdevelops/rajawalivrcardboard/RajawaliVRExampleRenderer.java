package com.calebdevelops.rajawalivrcardboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.Matrix;
import android.util.Log;

import com.calebdevelops.rawfinally.R;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import java.util.Arrays;

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
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderOBJ;
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
	private static final float YAW_LIMIT = 0.12f;
	private static final float PITCH_LIMIT = 0.12f;
	Object3D spaceship;
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
        if (isLookingAtObj(spaceship)){
			Log.v("debug", "message............");
        }
    }

	@Override
	public void initScene() {
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
			LoaderOBJ loaderobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.hellfire_obj);

			loaderobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.spaceship_obj);
			loaderobj.parse();
			spaceship = loaderobj.getParsedObject();
			spaceship.setY(-2);//height
			spaceship.setX(1);//right is positive, left is negative
			spaceship.setRotY(90);
			spaceship.setZ(-3);//front is negative   back is positive
			spaceship.setScale(0.2);

			getCurrentScene().addChild(spaceship);

			loaderobj = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.ewing_obj);
			loaderobj.parse();
			Object3D ewing = loaderobj.getParsedObject();
            ewing.getLookAt();
			ewing.setY(2);
			ewing.setX(0.5);
			ewing.setRotY(90);
			ewing.setZ(-5);
			ewing.setScale(0.3);
		//	getCurrentScene().addChild(ewing);


			//X: right
			//Y: height
			//Z: back


			CatmullRomCurve3D path1 = new CatmullRomCurve3D();
			path1.addPoint(new Vector3(0, -5, -10));//points that object will go through
			path1.addPoint(new Vector3(10, -5, 0));
			path1.addPoint(new Vector3(0, -4, 8));
			path1.addPoint(new Vector3(-16, -6, 0));
			path1.isClosedCurve(true);

			CatmullRomCurve3D path2 = new CatmullRomCurve3D();
			path2.addPoint(new Vector3(-10, 2, -30));//points that object will go through
			path2.addPoint(new Vector3(-10, 0, -20));
			path2.addPoint(new Vector3(-10, -2, -0));
			path2.addPoint(new Vector3(5, -2, 20));
			path2.addPoint(new Vector3(10, 0, 0));
			path2.addPoint(new Vector3(10, -2, -10));
			path2.addPoint(new Vector3(10, -5, -30));
			path2.isClosedCurve(true);
			SplineTranslateAnimation3D anim2 = new SplineTranslateAnimation3D(path2);
			anim2.setDurationMilliseconds(88000);
			anim2.setRepeatMode(Animation.RepeatMode.INFINITE);


			SplineTranslateAnimation3D anim=anim2;
			// -- orient to path
			anim.setOrientToPath(true);
			anim.setTransformable3D(spaceship);
			getCurrentScene().registerAnimation(anim);
			anim.play();
			//getCurrentScene().removeChild(ewing);
		} catch(Exception e) {
			e.printStackTrace();
		}

		super.initScene();
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

	public CatmullRomCurve3D choosepath(int n){
		CatmullRomCurve3D path = new CatmullRomCurve3D();
		switch (n){
			case 1:{
				path.addPoint(new Vector3(0, -5, -10));//points that object will go through
				path.addPoint(new Vector3(10, -5, 0));
				path.addPoint(new Vector3(0, -4, 8));
				path.addPoint(new Vector3(-16, -6, 0));
				break;
			}
			case 2:{
				path.addPoint(new Vector3(-10, 2, -30));//points that object will go through
				path.addPoint(new Vector3(-10, 0, -20));
				path.addPoint(new Vector3(-10, -2, -0));
				path.addPoint(new Vector3(5, -2, 20));
				path.addPoint(new Vector3(10, 0, 0));
				path.addPoint(new Vector3(10, -2, -10));
				path.addPoint(new Vector3(10, -5, -30));
				break;
			}
			case 3:{
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
		Log.v("At center? ", result);
    	return bool;
	}

}
