
package tbmelee;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.anim.AnimComposer;

public class GameEntity {

    private int tileX;
    private int tileZ;

    private float width;
    private float height;
    private float depth;
    
    private final boolean enemy;

    //Stance
    private static final float CROUCH_HEIGHT = 1f;
    private static final float STAND_HEIGHT = 2f;
    private boolean standing = true;

    //Visual
    private UnitVisual visual;
    private AnimComposer animComposer;

    //Combat
    private int hitPoints = 1;
    private int damageOutput = 1;
    private int weaponReach = 1;
    private GameEntity attackTarget;
    

    public GameEntity(int tileX, int tileZ,
                      float width, float height, float depth, boolean enemy) {
        this.tileX = tileX;
        this.tileZ = tileZ;

        this.width = width;
        this.height = height;
        this.depth = depth;
        this.enemy = enemy;
    }

    public Node createGeometry(AssetManager assetManager, boolean isEnemy) {
        visual = new UnitVisual(assetManager, width, height, depth, isEnemy, STAND_HEIGHT, getHeight());
        return visual.getNode();
        
    }

    public void setSelected(boolean selected) {
        visual.setSelected(selected, enemy);
    }

    //Movement
    public void moveTo(int tileX, int tileZ, Vector3f position) {
        this.tileX = tileX;
        this.tileZ = tileZ;
        getUnitNode().setLocalTranslation(position.x, 0f, position.z);
    }

    public boolean moveTowards(
        int tileX, int tileZ, Vector3f target, float maxDistance) {
        Vector3f current = getUnitNode().getLocalTranslation();
        float dx = target.x - current.x;
        float dz = target.z - current.z;
        float distance = (float) Math.sqrt(dx * dx + dz * dz);

        if (dx != 0f || dz != 0f) {
            visual.faceTowards(dx, dz);
        }

        if (distance <= maxDistance || distance == 0f) {
            getUnitNode().setLocalTranslation(target.x, 0f, target.z);
            this.tileX = tileX;
            this.tileZ = tileZ;
            return true;
        }

        float fraction = maxDistance / distance;
        getUnitNode().setLocalTranslation(
            current.x + dx * fraction,
            0f,
            current.z + dz * fraction
        );
        return false;
    }

    public void crouch() {
        setStanding(false);
    }

    public void stand() {
        setStanding(true);
    }

    private void setStanding(boolean standing) {
        if (this.standing == standing) {
            return;
        }

        this.standing = standing;
        visual.updateHeight(getHeight());
    }

    public void faceTowards(GameEntity target) {
        Vector3f from = getUnitNode().getLocalTranslation();
        Vector3f to = target.getUnitNode().getLocalTranslation();

        float dx = to.x - from.x;
        float dz = to.z - from.z;

        if (dx != 0f || dz != 0f) {
            visual.faceTowards(dx, dz);
        }
    }

    //Combat
    public void applyDamage(int damage) {
        hitPoints -= damage;
        System.out.println("Unit Hitpoints: " + hitPoints);
    }

    public void playAttackAnimation(GameEntity target) {
        visual.playAttackAnimation(() -> target.applyDamage(damageOutput));
    }

    public void applyAttackDamage() {
        if (attackTarget == null) {
            return;
        }

        attackTarget.applyDamage(damageOutput);
        attackTarget = null;
        animComposer.setCurrentAction("Idle");
    }

    // Getters and setters
    public int getTileX() {
        return tileX;
    }

    public int getTileZ() {
        return tileZ;
    }

    public Geometry getGeometry() {
        return visual.getGeometry();
    }

    public Node getUnitNode() {
        return visual.getNode();
    }

    public int getHeight() {
        return standing ? (int) STAND_HEIGHT : (int) CROUCH_HEIGHT;
    }

    public boolean isEnemy() {
        return enemy;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getDamageOutput() {
        return damageOutput;
    }

    public int getWeaponReach() {
        return weaponReach;
    }

    public void setWalking(boolean walking) {
        visual.setWalking(walking);
    }

    public boolean isAttackAnimationPlaying() {
        return visual.isAttackAnimationPlaying();
    }
}
