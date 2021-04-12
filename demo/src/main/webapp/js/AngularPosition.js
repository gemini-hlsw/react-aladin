// Extracted and improved from healpix.min.js
const AngularPosition = (function() {
  const AngularPosition = function(t, s) {
    this.theta = t;
    this.phi = s;
  };
  AngularPosition.prototype.toString = function() {
    return "theta: " + this.theta + ", phi: " + this.phi;
  } ;
  return AngularPosition;
  })();

export default AngularPosition;
