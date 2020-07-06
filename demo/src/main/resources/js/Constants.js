// Extracted and improved from healpix.min.js
const Constants = (function() {
  const Constants = {};
  Constants.PI = Math.PI;
  Constants.C_PR = Math.PI / 180;
  Constants.VLEV = 2;
  Constants.EPS = 1e-7;
  Constants.c = 0.105;
  Constants.LN10 = Math.log(10);
  Constants.PIOVER2 = Math.PI / 2;
  Constants.TWOPI = 2 * Math.PI;
  Constants.TWOTHIRD = 2 / 3;
  Constants.ARCSECOND_RADIAN = 484813681109536e-20;

  return Constants;
})();

export default Constants;
