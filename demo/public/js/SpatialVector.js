import Constants from "./Constants";

export default class SpatialVector {
    constructor(x, y, z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ra_ = 0;
        this.dec_ = 0;
        this.okRaDec_ = false;
    }
    setXYZ(t, s, i) {
        this.x = t;
        this.y = s;
        this.z = i;
        this.okRaDec_ = false;
    }

    length() {
        return Math.sqrt(this.lengthSquared());
    }

    lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    normalized() {
        const vectorLength = this.length();
        this.x /= vectorLength;
        this.y /= vectorLength;
        this.z /= vectorLength;
    }

    set(lon, lat) {
        this.ra_ = lon;
        this.dec_ = lat;
        this.okRaDec_ = true;
        this.updateXYZ();
    }

    angle(v1) {
        const xx = this.y * v1.z - this.z * v1.y;
        const yy = this.z * v1.x - this.x * v1.z;
        const zz = this.x * v1.y - this.y * v1.x;
        const cross = Math.sqrt(xx * xx + yy * yy + zz * zz);
        return Math.abs(Math.atan2(cross, this.dot(v1)));
    }

    get() { return [this.x, this.y, this.z]; }

    toString() { return 'SpatialVector[' + this.x + ', ' + this.y + ', ' + this.z + ']'; }

    cross(v) {
        return new SpatialVector(this.y * v.z - v.y * this.z, this.z * v.x - v.z * this.x, this.x * v.y - v.x() * this.y);
    }

    equal(other) {
        return Boolean(this.x===other.x && this.y===other.y && this.z===other.z);
    }

    mult(s) {
        return new SpatialVector(s * this.x, s * this.y, s * this.z);
    }

    dot(v1) {
        return this.x * v1.x + this.y * v1.y + this.z * v1.z;
    }

    add(s) {
        return new SpatialVector(this.x + s.x, this.y + s.y, this.z + s.z);
    }

    sub(s) {
        return new SpatialVector(this.x - s.x, this.y - s.y, this.z - s.z);
    }

    dec() {
        if (this.okRaDec_) return this.dec_;
        this.normalized();
        this.updateRaDec();
        return this.dec_;
    }

    ra() {
        if (this.okRaDec_) return this.ra_;
        this.normalized();
        this.updateRaDec();
        return this.ra_;
    }

    updateXYZ() {
        const t = Math.cos(this.dec_ * Constants.C_PR);
        this.x = Math.cos(this.ra_ * Constants.C_PR) * t;
        this.y = Math.sin(this.ra_ * Constants.C_PR) * t;
        this.z = Math.sin(this.dec_ * Constants.C_PR);
    }

    updateRaDec() {
        this.dec_ = Math.asin(this.z) / Constants.C_PR;
        const t = Math.cos(this.dec_ * Constants.C_PR);
        this.ra_ = (t > Constants.EPS || -Constants.EPS > t) ?
            this.y > Constants.EPS || this.y < -Constants.EPS ? 0 > this.y ?
                360 - Math.acos(this.x / t) / Constants.C_PR : Math.acos(this.x / t) / Constants.C_PR : 0 > this.x ?
                180 :
                0 :
            0;
        this.okRaDec_ = true;
    }
}

