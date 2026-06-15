function AuthPanel({ title, subtitle }) {
  return (
    <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
      <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
      <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

      <div className="flex items-center gap-3 z-10">
        <div className="w-9 h-9 bg-blue-500 rounded-xl flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
          </svg>
        </div>
        <span className="text-white text-base font-medium">Wallet</span>
      </div>

      <div className="z-10">
        <h2 className="text-white text-2xl font-medium leading-snug mb-3">
          {title}
        </h2>
        <p className="text-slate-400 text-sm leading-relaxed">
          {subtitle}
        </p>
      </div>

      <div className="z-10">
        <p className="text-slate-600 text-xs">Wallet App · v1.0</p>
      </div>
    </div>
  )
}

export default AuthPanel