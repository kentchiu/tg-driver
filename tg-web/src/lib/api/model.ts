export interface PagingData<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  orders: string[];
  optimizeCountSql: boolean;
  hitCount: boolean;
  countId: null;
  maxLimit: null;
  searchCount: boolean;
  pages: number;
}

export interface ErrorMessage {
  /**
   * http status
   */
  code: number;
  /**
   * error message
   */
  message: string;
  /**
   * other key-value pair message
   */
  [key: string]: any;
}
