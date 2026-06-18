import NovaLogo from './NovaLogo'

function AuthPanel({ title, subtitle }) {
  return (
    <div className="hidden md:flex w-5/12 bg-slate-900 flex-col justify-between p-10 relative overflow-hidden">
      <div className="absolute -top-20 -right-20 w-60 h-60 rounded-full bg-blue-900 opacity-40" />
      <div className="absolute -bottom-16 -left-16 w-44 h-44 rounded-full bg-blue-900 opacity-30" />

      <NovaLogo />

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