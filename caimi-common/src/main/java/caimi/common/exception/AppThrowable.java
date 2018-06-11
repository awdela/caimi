//package caimi.common.exception;
//
//public interface AppThrowable {
//	
//	public int getCode();
//	
//	public String getMessage();
//	
//	public Object[] getParameters();
//	
//	public StackTraceElement getCallerStackTrace();
//	
//	public static String getCallerClass() {
//		Exception e = new Exception();
//		for(StackTraceElement elem: e.getStackTrace()) {
//			String className = elem.getClassName();
//			if( !className.equals(AppThrowable.class.getName())
//				&& !className.equals(AppException.class.getName())
//				&& !className.equals(AppRuntimeException.class.getName())) {
//				return className;
//			}
//		}
//		return null;
//	}
//
//	public static String errorcode2str(int errorcode) {
//		return String.format("%08x", errorcode);
//	}
//}
