package org.andengine.testscrollscene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.scrollscene.ScrollScene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.graphics.Typeface;
import android.widget.Toast;


public class TestScrollScene extends SimpleBaseGameActivity{
	// ===========================================================
	// Constants
	// ===========================================================
	private static final float CAMERA_WIDTH = 1196;
	private static final float CAMERA_HEIGHT = 720;
	
	// ===========================================================
	// Fields
	// ===========================================================

	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mFace1TextureRegion;
	private ITextureRegion mFace2TextureRegion;
	private ITextureRegion mFace3TextureRegion;
	private Font mFont;

	
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 512);
		this.mFace1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png");
		this.mFace2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png");
		this.mFace3TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "face_hexagon_tiled.png");

		try {
			this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			this.mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, Color.YELLOW_ABGR_PACKED_INT);
		this.mFont.load();
		
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final ScrollScene scene = new ScrollScene(CAMERA_WIDTH * 0.75f, CAMERA_HEIGHT);
		//the offset represents how much the layers overlap
		scene.setOffset(0);
		
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		/* Calculate the coordinates for the face, so its centered on the camera. */
		final float centerX = (scene.getPageWidth() - this.mFace1TextureRegion.getWidth()) / 2;
		final float centerY = (scene.getPageHeight() - this.mFace1TextureRegion.getHeight()) / 2;

		/**
		 * This looks stupid, but the main reason why I'm doing this is because Entity doesn't have width/height in gles2 branch
		 * here I use a rectangle since it has width and height and it's also easy to work with
		 * but you can use any variation of RectangularShape i.e. Sprite
		 **/
		Rectangle page1 = new Rectangle(0, 0, 0, 0, this.getVertexBufferObjectManager());
		
		Rectangle page2 = new Rectangle(0, 0, 0, 0, this.getVertexBufferObjectManager());
		page2.setColor(Color.RED);
		
		/* Create the button and add it to the scene. */
		final Sprite face = new ButtonSprite(centerX, centerY, this.mFace1TextureRegion, this.mFace2TextureRegion, this.mFace3TextureRegion, this.getVertexBufferObjectManager(), new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(TestScrollScene.this, "Clicked", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		//You can add whatever you want (sprites, buttonSprites, text, menus etc)
		final Sprite face2 = new Sprite(centerX, centerY, this.mFace3TextureRegion, this.getVertexBufferObjectManager());
		
		final Text text = new Text(250, 240, this.mFont, "Hello !", this.getVertexBufferObjectManager());
		
		scene.registerTouchArea(face);
		page1.attachChild(face);
		page2.attachChild(face2);
		page2.attachChild(text);
		
		scene.addPage(page1);
		scene.addPage(page2);
		
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		return scene;
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}