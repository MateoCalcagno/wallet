function AuthPanel({ title, subtitle }) {
  return (
    <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
      <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
      <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

      <div className="flex items-center gap-3 z-10">
        <svg width="38" height="38" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
          <rect width="48" height="48" rx="10" fill="#1a3a6e"/>
          <path d="M24 6 L29 18 L41 22 L29 26 L24 38 L19 26 L7 22 L19 18 Z" fill="#3d8ef8"/>
          <circle cx="24" cy="22" r="3.5" fill="#6eb4ff"/>
        </svg>
        <div className="flex flex-col leading-tight">
          <span className="text-white text-lg font-bold tracking-widest">NOVA</span>
          <span className="text-blue-400 text-[10px] tracking-[0.3em]">WALLET</span>
        </div>
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
        <p className="text-slate-600 text-xs">Nova Wallet · v1.0</p>
        <p className="text-slate-600 text-xs">Developed by Mateo Calcagno</p>
      </div>
    </div>
  )
}

export default AuthPanel