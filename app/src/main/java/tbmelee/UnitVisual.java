package tbmelee;


import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Box;

public class UnitVisual {
    
    private final Node unitNode = new Node("Unit");
    private Geometry pickingGeometry;
    private AnimComposer animComposer;
    private Geometry frameGeometry;
    private Material frameMaterial;
    private Spatial model;

    private boolean walking;

    private Runnable attackCompletion;

    public UnitVisual(AssetManager assetManager, float width, float height, float depth, boolean isEnemy, float standHeight, float getHeight) {
        Box box = new Box(width / 2f, height / 2f, depth / 2f);
        pickingGeometry = new Geometry("UnitPickingBox", box);

        pickingGeometry.setCullHint(Spatial.CullHint.Always);

        unitNode.attachChild(pickingGeometry);
        createFrame(assetManager, width, height, depth, isEnemy);

        model = new ModelLoader().loadModel(assetManager, isEnemy);
        animComposer = findAnimComposer(model);

        //Manual check to console
        if (animComposer != null) {
            System.out.println(
                "Clips in placeholder_guy1.glb: "
                    + animComposer.getAnimClipsNames()
            );
        }

        //Check animation clips
        if (animComposer == null || !animComposer.hasAnimClip("ArmatureAction")) {
            throw new IllegalStateException(
                "Model is missing AnimComposer or ArmatureAction"
            );
        }

        if (!animComposer.hasAnimClip("Attack")) {
            throw new IllegalStateException("Model is missing Attack animation");
        }

        animComposer.actionSequence(
            "AttackOnce",
            animComposer.action("Attack"),
            Tweens.callMethod(this, "finishAttack")
        );

        model.setName("UnitModel");
        model.updateGeometricState();

        // Set the model's scale and local position after checking its size and origin.
        BoundingBox bounds = (BoundingBox) model.getWorldBound();

        float modelWidth = bounds.getXExtent() * 2f;
        float modelHeight = bounds.getYExtent() * 2f;
        float modelDepth = bounds.getZExtent() * 2f;

        float margin = 0.9f;
        float modelScale = Math.min(
            margin * width / modelWidth,
            Math.min(
                margin * standHeight / modelHeight,
                margin * depth / modelDepth
            )
        );

        Vector3f center = bounds.getCenter().clone();
        Vector3f min = bounds.getMin(new Vector3f());

        model.setLocalScale(modelScale);
        model.setLocalTranslation(
            -center.x * modelScale,
            -min.y * modelScale,
            -center.z * modelScale
        );
        unitNode.attachChild(model);
        updateHeight(getHeight);
    }
    
    private AnimComposer findAnimComposer(Spatial spatial) {
        AnimComposer composer = spatial.getControl(AnimComposer.class);
        if (composer != null) {
            return composer;
        }

        if (spatial instanceof Node node) {
            for (Spatial child : node.getChildren()) {
                composer = findAnimComposer(child);
                if (composer != null) {
                    return composer;
                }
            }
        }

        return null;
    }

    private void createFrame(AssetManager assetManager, float width, float height, float depth, boolean enemy) {
        Vector3f[] corners = new Vector3f[8];

        for (int i = 0; i < corners.length; i++) {
            float x = (i & 1) == 0 ? -width / 2f : width / 2f;
            float y = (i & 2) == 0 ? -height / 2f : height / 2f;
            float z = (i & 4) == 0 ? -depth / 2f : depth / 2f;
            corners[i] = new Vector3f(x, y, z);
        }

        int[][] edges = {
            {0, 1}, {2, 3}, {4, 5}, {6, 7},
            {0, 2}, {1, 3}, {4, 6}, {5, 7},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        Vector3f[] endpoints = new Vector3f[edges.length * 2];
        int index = 0;

        for (int[] edge : edges) {
            endpoints[index++] = corners[edge[0]];
            endpoints[index++] = corners[edge[1]];
        }

        Mesh mesh = new Mesh();
        mesh.setMode(Mesh.Mode.Lines);
        mesh.setBuffer(
            VertexBuffer.Type.Position,
            3,
            BufferUtils.createFloatBuffer(endpoints)
        );
        mesh.updateBound();

        frameGeometry = new Geometry("UnitFrame", mesh);
        frameMaterial = new Material(
            assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md"
        );
        frameMaterial.setColor("Color", enemy ? ColorRGBA.Red : ColorRGBA.Blue);
        frameGeometry.setMaterial(frameMaterial);
        unitNode.attachChild(frameGeometry);
    }

        public void updateHeight(float getHeight) {
        float verticalScale = getHeight;
        float centerY = getHeight / 2f;
        unitNode.setLocalScale(1f, 1f, 1f);

        frameGeometry.setLocalScale(1f, verticalScale, 1f);
        frameGeometry.setLocalTranslation(0f, centerY, 0f);

        pickingGeometry.setLocalScale(1f, verticalScale, 1f);
        pickingGeometry.setLocalTranslation(0f, centerY, 0f);
    }

    public void playAttackAnimation(Runnable onComplete) {
        attackCompletion = onComplete;
        animComposer.setCurrentAction(
            "AttackOnce",
            AnimComposer.DEFAULT_LAYER,
            false
        );
    }

    public void finishAttack() {
        animComposer.setCurrentAction("Idle");

        Runnable completion = attackCompletion;
        attackCompletion = null;

        if (completion != null) {
            completion.run();
        }
    }

    public void setSelected(boolean selected, boolean enemy) {
        frameMaterial.setColor(
            "Color",
            selected
                ? new ColorRGBA(0.5f, 0f, 0.5f, 1f)
                : enemy ? ColorRGBA.Red : ColorRGBA.Blue
        );
    }

        public void setWalking(boolean walking) {
        if (this.walking == walking) {
            return;
        }

        this.walking = walking;
        animComposer.setCurrentAction(walking ? "ArmatureAction" : "Idle");
    }

    public void faceTowards(float dx, float dz) {
        if (dx != 0f || dz != 0f) {
            float yaw = (float) Math.atan2(dx, dz);
            model.setLocalRotation(
                new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y)
            );
        }
    }

    //Getters and Setters
    public Node getNode() {
        return unitNode;
    }

    public Geometry getGeometry() {
        return pickingGeometry;
    }
    
}
