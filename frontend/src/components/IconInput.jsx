function IconInput({ iconPath, className = '', ...props }) {
  return (
    <div className="relative">
      <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 pointer-events-none">
        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none"
          viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round" d={iconPath} />
        </svg>
      </span>
      <input
        {...props}
        className={`w-full pl-9 pr-3 py-2.5 text-sm border border-gray-200 rounded-lg bg-gray-50
          focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition ${className}`}
      />
    </div>
  )
}

export default IconInput