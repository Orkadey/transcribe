package transcribe;

class HandlerBuilder {
	
	private String _excelFile;
	private int _col = 1, _fRow = 1, _lRow = 100, _sheetNum = 1;
	
	public HandlerBuilder() {}
	
	public RequestHandler buildRequestHandler () {
		return new RequestHandler(_excelFile, _col, _fRow, _lRow, _sheetNum);
	}
	
	public HandlerBuilder excelFile(String _excelFile) {
		this._excelFile = _excelFile;
		return this;
	}
	
	public HandlerBuilder col(int _col) {
		this._col = _col;
		return this;
	}
	
	public HandlerBuilder firstRow(int _fRow) {
		this._fRow = _fRow;
		return this;
	}
	
	public HandlerBuilder lastRow(int _lRow) {
		this._lRow = _lRow;
		return this;
	}
	
	public HandlerBuilder sheet(int _sheetNum) {
		this._sheetNum = _sheetNum;
		return this;
	}

}
